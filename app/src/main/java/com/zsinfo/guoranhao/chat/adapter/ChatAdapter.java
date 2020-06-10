package com.zsinfo.guoranhao.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.zsinfo.guoranhao.R;
import com.zsinfo.guoranhao.chat.activity.PicInfoActivity;
import com.zsinfo.guoranhao.chat.bean.ChatMessageBean;
import com.zsinfo.guoranhao.chat.utils.MediaManager;
import com.zsinfo.guoranhao.chat.utils.ScreenUtil;
import com.zsinfo.guoranhao.chat.utils.TimeUtils;
import com.zsinfo.guoranhao.chat.widget.BubbleImageView;
import com.zsinfo.guoranhao.chat.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lixl on 2018/7/6.
 *
 * 聊天界面 适配器
 */
public class ChatAdapter extends BaseAdapter {

    private Context context;
    private List<ChatMessageBean> chats = new ArrayList<>();
    private String fromJid = "";
    private String toJid = "";
    private String fromUser = "";  //发送者的别名
    private String toUser = "";  //接收者
    private String userLogo = "";  //url路径
    private Bitmap userLogoBitmap; //下载之后的
    public static final int SEND_USER_MSG = 0;//发送文本
    public static final int RECEIVE_USER_MSG = 1;    //接收文本
    public static final int SEND_USER_IMG = 2;//发送图片
    public static final int RECEIVE_USER_IMG = 3;    //接收图片
    public static final int SEND_USER_VOICE = 4;//发送语音
    public static final int RECEIVE_USER_VOICE = 5;  //接收语音

    private LayoutInflater mLayoutInflater;

    private int voicePlayPosition = -1;
    private int mMinItemWith;// 设置对话框的最大宽度和最小宽度
    private int mMaxItemWith;
    private VoiceIsRead voiceIsRead;
    public List<String> unReadPosition = new ArrayList<String>();

    public ChatAdapter(Context context){
        this.context = context;
        load();
    }

    public ChatAdapter(Context context, List<ChatMessageBean> chats){
        this.context = context;
        this.chats = chats;
        load();
    }

    public ChatAdapter(Context context, List<ChatMessageBean> chats,
                       String fromJid, String toJid, String fromUser, String toUser,
                       String userLogo, Bitmap userLogoBitmap){
        this.context = context;
        this.chats = chats;
        this.fromJid = fromJid;
        this.toJid = toJid;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.userLogo = userLogo;
        this.userLogoBitmap = userLogoBitmap;
        load();
    }

    public interface VoiceIsRead {
        public void voiceOnClick(int position);
    }

    public void setVoiceIsReadListener(VoiceIsRead voiceIsRead) {
        this.voiceIsRead = voiceIsRead;
    }

    private void load(){
        mLayoutInflater = LayoutInflater.from(context);
        // 获取系统宽度
        WindowManager wManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wManager.getDefaultDisplay().getMetrics(outMetrics);
        mMaxItemWith = (int) (outMetrics.widthPixels * 0.5f);
        mMinItemWith = (int) (outMetrics.widthPixels * 0.15f);
    }

    /**
     * 添加多条消息记录，并且刷新
     * @param chats
     */
    public void setChats(List<ChatMessageBean> chats) {
        this.chats.addAll(0, chats);  //分页加载数据，都从第一个消息开始
        notifyDataSetChanged();
    }

    /**
     * 添加一条消息记录，并且刷新
     * @param chat
     */
    public void addChat(ChatMessageBean chat){
        chats.add(chat);  //添加一条新消息，放入到最后一条
        notifyDataSetChanged();
    }

    /**
     * 录音之前，先前播放的音乐需要停止
     */
    public void stopPlayVoice() {
        if (voicePlayPosition != -1) {
            View voicePlay = (View) ((Activity) context).findViewById(voicePlayPosition);
            if (voicePlay != null) {
                if (getItemViewType(voicePlayPosition) == RECEIVE_USER_VOICE) {
                    voicePlay.setBackgroundResource(R.mipmap.audio_animation_list_left_3);
                } else {
                    voicePlay.setBackgroundResource(R.mipmap.audio_animation_list_right_3);
                }
            }
            MediaManager.pause();
            voicePlayPosition = -1;
        }
    }

    @Override
    public int getCount() {
        return chats.size();
    }

    @Override
    public Object getItem(int position) {
        return chats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (chats.get(position).getMsgType().equals("text")) {
            if (chats.get(position).getFromUser().equals(fromJid)) {
                return SEND_USER_MSG;
            } else {
                return RECEIVE_USER_MSG;
            }
        } else if (chats.get(position).getMsgType().equals("image")) {
            if (chats.get(position).getFromUser().equals(fromJid)) {
                return SEND_USER_IMG;
            } else {
                return RECEIVE_USER_IMG;
            }
        } else if (chats.get(position).getMsgType().equals("voice")) {
            if (chats.get(position).getFromUser().equals(fromJid)) {
                return SEND_USER_VOICE;
            } else {
                return RECEIVE_USER_VOICE;
            }
        }
        return 123456;
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessageBean chat = chats.get(position);
        switch (getItemViewType(position)){
            case SEND_USER_MSG:
                FromUserMsgViewHolder holder1;
                if (convertView == null){
                    holder1 = new FromUserMsgViewHolder();
                    convertView = mLayoutInflater.inflate(R.layout.item_chat_msg_right, null);
                    holder1.chat_time = (TextView) convertView.findViewById(R.id.mychat_time);
                    holder1.headicon = (CircleImageView) convertView.findViewById(R.id.iv_my_user_icon);
                    holder1.myname = (TextView) convertView.findViewById(R.id.tv_myname);
                    holder1.mytime = (TextView) convertView.findViewById(R.id.tv_mytime);
                    holder1.msg_content = (TextView) convertView.findViewById(R.id.mycontent);
                    convertView.setTag(holder1);
                } else {
                    holder1 = (FromUserMsgViewHolder) convertView.getTag();
                }
                sendMsgUserLayout((FromUserMsgViewHolder) holder1, chat, position);
                break;
            case RECEIVE_USER_MSG:
                ToUserMsgViewHolder holder2;
                if (convertView == null){
                    holder2 = new ToUserMsgViewHolder();
                    convertView = mLayoutInflater.inflate(R.layout.item_chat_msg_left, null);
                    holder2.chat_time = (TextView) convertView.findViewById(R.id.chat_time);
                    holder2.headicon = (CircleImageView) convertView.findViewById(R.id.iv_other_user_icon);
                    holder2.othername = (TextView) convertView.findViewById(R.id.tv_othername);
                    holder2.othertime = (TextView) convertView.findViewById(R.id.tv_othertime);
                    holder2.msg_content = (TextView) convertView.findViewById(R.id.content);
                    convertView.setTag(holder2);
                } else {
                    holder2 = (ToUserMsgViewHolder) convertView.getTag();
                }
                receiveMsgUserLayout((ToUserMsgViewHolder) holder2, chat, position);
                break;
            case SEND_USER_IMG:
                FromUserImageViewHolder holder3;
                if (convertView == null) {
                    holder3 = new FromUserImageViewHolder();
                    convertView = mLayoutInflater.inflate(R.layout.item_chat_image_right, null);
                    holder3.chat_time = (TextView) convertView.findViewById(R.id.mychat_time);
                    holder3.headicon = (CircleImageView) convertView.findViewById(R.id.iv_my_user_icon);
                    holder3.myname = (TextView) convertView.findViewById(R.id.tv_myname);
                    holder3.mytime = (TextView) convertView.findViewById(R.id.tv_mytime);
                    holder3.msg_image = (BubbleImageView) convertView.findViewById(R.id.image_message);
                    convertView.setTag(holder3);
                } else {
                    holder3 = (FromUserImageViewHolder) convertView.getTag();
                }
                sendImgUserLayout((FromUserImageViewHolder) holder3, chat, position);
                break;
            case RECEIVE_USER_IMG:
                ToUserImgViewHolder holder4;
                if (convertView == null) {
                    holder4 = new ToUserImgViewHolder();
                    convertView = mLayoutInflater.inflate(R.layout.item_chat_image_left, null);
                    holder4.chat_time = (TextView) convertView.findViewById(R.id.chat_time);
                    holder4.headicon = (CircleImageView) convertView.findViewById(R.id.iv_other_user_icon);
                    holder4.othername = (TextView) convertView.findViewById(R.id.tv_othername);
                    holder4.othertime = (TextView) convertView.findViewById(R.id.tv_othertime);
                    holder4.msg_image = (BubbleImageView) convertView.findViewById(R.id.image_message);
                    convertView.setTag(holder4);
                } else {
                    holder4 = (ToUserImgViewHolder) convertView.getTag();
                }
                receiveImgUserLayout((ToUserImgViewHolder) holder4, chat, position);
                break;
            case SEND_USER_VOICE:
                FromUserVoiceViewHolder holder5;
                if (convertView == null) {
                    holder5 = new FromUserVoiceViewHolder();
                    convertView = mLayoutInflater.inflate(R.layout.item_chat_voice_right, null);
                    holder5.chat_time = (TextView) convertView.findViewById(R.id.mychat_time);
                    holder5.headicon = (CircleImageView) convertView.findViewById(R.id.iv_my_user_icon);
                    holder5.myname = (TextView) convertView.findViewById(R.id.tv_myname);
                    holder5.mytime = (TextView) convertView.findViewById(R.id.tv_mytime);
                    holder5.voice_group = (LinearLayout) convertView.findViewById(R.id.voice_group);
                    holder5.voice_time = (TextView) convertView.findViewById(R.id.voice_time);
                    holder5.voice_anim = (View) convertView.findViewById(R.id.id_recorder_anim);
                    convertView.setTag(holder5);
                } else {
                    holder5 = (FromUserVoiceViewHolder) convertView.getTag();
                }
                sendVoiceUserLayout((FromUserVoiceViewHolder) holder5, chat, position);
                break;
            case RECEIVE_USER_VOICE:
                ToUserVoiceViewHolder holder6;
                if (convertView == null) {
                    holder6 = new ToUserVoiceViewHolder();
                    convertView = mLayoutInflater.inflate(R.layout.item_chat_voice_left, null);
                    holder6.chat_time = (TextView) convertView.findViewById(R.id.chat_time);
                    holder6.headicon = (CircleImageView) convertView.findViewById(R.id.iv_other_user_icon);
                    holder6.othername = (TextView) convertView.findViewById(R.id.tv_othername);
                    holder6.othertime = (TextView) convertView.findViewById(R.id.tv_othertime);
                    holder6.voice_group = (LinearLayout) convertView.findViewById(R.id.voice_group);
                    holder6.voice_time = (TextView) convertView.findViewById(R.id.voice_time);
                    holder6.voice_anim = (View) convertView .findViewById(R.id.id_receiver_recorder_anim);
                    holder6.receiver_voice_unread = (View) convertView.findViewById(R.id.receiver_voice_unread);
                    convertView.setTag(holder6);
                } else {
                    holder6 = (ToUserVoiceViewHolder) convertView.getTag();
                }
                receiveVoiceUserLayout((ToUserVoiceViewHolder) holder6, chat, position);
                break;
        }
        return convertView;
    }

    private void sendMsgUserLayout(final FromUserMsgViewHolder holder, final ChatMessageBean chatBean, final int position) {
        /* time */
        long msgCreateTime = chatBean.getCreateTime();
        long currentTime = System.currentTimeMillis();
        if (TimeUtils.isSameYear(msgCreateTime)) {
            if (TextUtils.equals(TimeUtils.getDatas(msgCreateTime), TimeUtils.getDatas(currentTime))) {
                holder.chat_time.setText(TimeUtils.getDate(msgCreateTime));
            } else {
                holder.chat_time.setText(TimeUtils.geTimeNoS(msgCreateTime));
            }
        } else {
            holder.chat_time.setText(TimeUtils.getTime(msgCreateTime));
        }
        //holder.chat_time.setVisibility(View.VISIBLE);

        holder.myname.setText(fromUser);
        holder.mytime.setText(TimeUtils.geTimeNoS(chatBean.getCreateTime()));
        //默认情况下，Picasso 内存缓存和磁盘缓存都开启了
        if (userLogoBitmap != null){
            holder.headicon.setImageBitmap(userLogoBitmap);
        } else {
            Glide.with(context)
                    .load(userLogo)
                    .error(R.mipmap.default_avatar)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//跳过内存缓存
                    //.networkPolicy(NetworkPolicy.NO_CACHE)//跳过磁盘缓存
                    .into(holder.headicon);
        }

        holder.msg_content.setVisibility(View.VISIBLE);
        holder.msg_content.setText(chatBean.getContent());
    }

    private void receiveMsgUserLayout(final ToUserMsgViewHolder holder, final ChatMessageBean chatBean, final int position) {
        /* time */
        long msgCreateTime = chatBean.getCreateTime();
        long currentTime = System.currentTimeMillis();
        if (TimeUtils.isSameYear(msgCreateTime)) {
            if (TextUtils.equals(TimeUtils.getDatas(msgCreateTime), TimeUtils.getDatas(currentTime))) {
                holder.chat_time.setText(TimeUtils.getDate(msgCreateTime));
            } else {
                holder.chat_time.setText(TimeUtils.geTimeNoS(msgCreateTime));
            }
        } else {
            holder.chat_time.setText(TimeUtils.getTime(msgCreateTime));
        }
        //holder.chat_time.setVisibility(View.VISIBLE);

        holder.othername.setText(toUser);
        holder.othertime.setText(TimeUtils.geTimeNoS(chatBean.getCreateTime()));

        holder.msg_content.setVisibility(View.VISIBLE);
        holder.msg_content.setText(chatBean.getContent());
    }

    private void sendImgUserLayout(final FromUserImageViewHolder holder, final ChatMessageBean chatBean, final int position) {
        //        switch (0) {
        //            case ChatConst.SENDING:
        //                an = AnimationUtils.loadAnimation(context,
        //                        R.anim.update_loading_progressbar_anim);
        //                LinearInterpolator lin = new LinearInterpolator();
        //                an.setInterpolator(lin);
        //                an.setRepeatCount(-1);
        //                holder.sendFailImg
        //                        .setBackgroundResource(R.mipmap.xsearch_loading);
        //                holder.sendFailImg.startAnimation(an);
        //                an.startNow();
        //                holder.sendFailImg.setVisibility(View.VISIBLE);
        //                break;
        //
        //            case ChatConst.COMPLETED:
        //                holder.sendFailImg.clearAnimation();
        //                holder.sendFailImg.setVisibility(View.GONE);
        //                break;
        //
        //            case ChatConst.SENDERROR:
        //                holder.sendFailImg.clearAnimation();
        //                holder.sendFailImg
        //                        .setBackgroundResource(R.mipmap.msg_state_fail_resend_pressed);
        //                holder.sendFailImg.setVisibility(View.VISIBLE);
        //                holder.sendFailImg.setOnClickListener(new View.OnClickListener() {
        //
        //                    @Override
        //                    public void onClick(View view) {
        //                        if (sendErrorListener != null) {
        //                            sendErrorListener.onClick(position);
        //                        }
        //                    }
        //                });
        //                break;
        //            default:
        //                break;
        //        }

        /* time */
        long msgCreateTime = chatBean.getCreateTime();
        long currentTime = System.currentTimeMillis();
        if (TimeUtils.isSameYear(msgCreateTime)) {
            if (TextUtils.equals(TimeUtils.getDatas(msgCreateTime), TimeUtils.getDatas(currentTime))) {
                holder.chat_time.setText(TimeUtils.getDate(msgCreateTime));
            } else {
                holder.chat_time.setText(TimeUtils.geTimeNoS(msgCreateTime));
            }
        } else {
            holder.chat_time.setText(TimeUtils.getTime(msgCreateTime));
        }
        //holder.chat_time.setVisibility(View.VISIBLE);

        holder.myname.setText(fromUser);
        holder.mytime.setText(TimeUtils.geTimeNoS(chatBean.getCreateTime()));
        if (userLogoBitmap != null){
            holder.headicon.setImageBitmap(userLogoBitmap);
        } else {
            Glide.with(context)
                    .load(userLogo)
                    .error(R.mipmap.default_avatar)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//跳过内存缓存
                    //.networkPolicy(NetworkPolicy.NO_CACHE)//跳过磁盘缓存
                    .into(holder.headicon);
        }

        final String image_url = chatBean.getContent().startsWith("http://") ?
                chatBean.getContent() : "http://" + chatBean.getContent();
        Picasso.with(context)
                .load(image_url)
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.mipmap.example)
//                .transform(new Transformation() {  //图片压缩，导致失帧
//                    @Override
//                    public Bitmap transform(Bitmap arg0) {
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        arg0.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                        byte[] b = baos.toByteArray();
//                        Bitmap bm = ChatUtils.handlerBitmap(b);
//                        if (bm != arg0) {
//                            arg0.recycle();
//                            System.gc();
//                            arg0 = null;
//                        }
//                        return bm;
//                    }
//
//                    @Override
//                    public String key() {
//                        return image_url;
//                    }
//                })
                .into(holder.msg_image);
        holder.msg_image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                stopPlayVoice();
                Intent intent = new Intent(context, PicInfoActivity.class);
                intent.putExtra("url", image_url);
                context.startActivity(intent);
            }

        });

    }

    private void receiveImgUserLayout(final ToUserImgViewHolder holder, final ChatMessageBean chatBean, final int position) {
        /* time */
        long msgCreateTime = chatBean.getCreateTime();
        long currentTime = System.currentTimeMillis();
        if (TimeUtils.isSameYear(msgCreateTime)) {
            if (TextUtils.equals(TimeUtils.getDatas(msgCreateTime), TimeUtils.getDatas(currentTime))) {
                holder.chat_time.setText(TimeUtils.getDate(msgCreateTime));
            } else {
                holder.chat_time.setText(TimeUtils.geTimeNoS(msgCreateTime));
            }
        } else {
            holder.chat_time.setText(TimeUtils.getTime(msgCreateTime));
        }
        //holder.chat_time.setVisibility(View.VISIBLE);

        holder.othername.setText(toUser);
        holder.othertime.setText(TimeUtils.geTimeNoS(chatBean.getCreateTime()));

        final String image_url = chatBean.getContent().startsWith("http://") ?
                chatBean.getContent() : "http://" + chatBean.getContent();
        Picasso.with(context)
                .load(image_url)
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.mipmap.example)
//                .transform(new Transformation() {   //图片压缩，导致失帧
//                    @Override
//                    public Bitmap transform(Bitmap arg0) {
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        arg0.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                        byte[] b = baos.toByteArray();
//                        Bitmap bm = ChatUtils.handlerBitmap(b);
//                        if (bm != arg0) {
//                            arg0.recycle();
//                            System.gc();
//                            arg0 = null;
//                        }
//                        return bm;
//                    }
//
//                    @Override
//                    public String key() {
//                        return image_url;
//                    }
//                })
                .into(holder.msg_image);
            holder.msg_image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    stopPlayVoice();
                    Intent intent = new Intent(context, PicInfoActivity.class);
                    intent.putExtra("url", image_url);
                    context.startActivity(intent);
                }

            });
        }

    private void sendVoiceUserLayout(final FromUserVoiceViewHolder holder, final ChatMessageBean chatBean, final int position) {
        //        switch (0) {
        //            case ChatConst.SENDING:
        //                an = AnimationUtils.loadAnimation(context,
        //                        R.anim.update_loading_progressbar_anim);
        //                LinearInterpolator lin = new LinearInterpolator();
        //                an.setInterpolator(lin);
        //                an.setRepeatCount(-1);
        //                holder.sendFailImg
        //                        .setBackgroundResource(R.mipmap.xsearch_loading);
        //                holder.sendFailImg.startAnimation(an);
        //                an.startNow();
        //                holder.sendFailImg.setVisibility(View.VISIBLE);
        //                break;
        //
        //            case ChatConst.COMPLETED:
        //                holder.sendFailImg.clearAnimation();
        //                holder.sendFailImg.setVisibility(View.GONE);
        //                break;
        //
        //            case ChatConst.SENDERROR:
        //                holder.sendFailImg.clearAnimation();
        //                holder.sendFailImg
        //                        .setBackgroundResource(R.mipmap.msg_state_fail_resend_pressed);
        //                holder.sendFailImg.setVisibility(View.VISIBLE);
        //                holder.sendFailImg.setOnClickListener(new View.OnClickListener() {
        //
        //                    @Override
        //                    public void onClick(View view) {
        //                        if (sendErrorListener != null) {
        //                            sendErrorListener.onClick(position);
        //                        }
        //                    }
        //
        //                });
        //                break;
        //        }

        /* time */
        long msgCreateTime = chatBean.getCreateTime();
        long currentTime = System.currentTimeMillis();
        if (TimeUtils.isSameYear(msgCreateTime)) {
            if (TextUtils.equals(TimeUtils.getDatas(msgCreateTime), TimeUtils.getDatas(currentTime))) {
                holder.chat_time.setText(TimeUtils.getDate(msgCreateTime));
            } else {
                holder.chat_time.setText(TimeUtils.geTimeNoS(msgCreateTime));
            }
        } else {
            holder.chat_time.setText(TimeUtils.getTime(msgCreateTime));
        }
        //holder.chat_time.setVisibility(View.VISIBLE);

        holder.myname.setText(fromUser);
        holder.mytime.setText(TimeUtils.geTimeNoS(chatBean.getCreateTime()));
        if (userLogoBitmap != null){
            holder.headicon.setImageBitmap(userLogoBitmap);
        } else {
            Glide.with(context)
                    .load(userLogo)
                    .error(R.mipmap.default_avatar)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//跳过内存缓存
                    //.networkPolicy(NetworkPolicy.NO_CACHE)//跳过磁盘缓存
                    .into(holder.headicon);
        }

        AnimationDrawable drawable;
        if (position == voicePlayPosition) {
            holder.voice_anim.setBackgroundResource(R.mipmap.audio_animation_list_right_3);
            holder.voice_anim.setBackgroundResource(R.drawable.voice_play_send);
            drawable = (AnimationDrawable) holder.voice_anim.getBackground();
            drawable.start();
        } else {
            holder.voice_anim.setBackgroundResource(R.mipmap.audio_animation_list_right_3);
        }
        holder.voice_anim.setId(position);  //防止数组下标越界，手动给当前语音布局设置id
        holder.voice_group.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                holder.voice_anim.setBackgroundResource(R.mipmap.audio_animation_list_right_3);
                stopPlayVoice();
                voicePlayPosition = holder.voice_anim.getId();
                AnimationDrawable drawable;
                holder.voice_anim.setBackgroundResource(R.drawable.voice_play_send);
                drawable = (AnimationDrawable) holder.voice_anim.getBackground();
                drawable.start();
                String voicePath = chatBean.getContent().startsWith("http://") ?
                        chatBean.getContent() : "http://" + chatBean.getContent();
                if (voiceIsRead != null) {
                    voiceIsRead.voiceOnClick(position);
                }
                MediaManager.playSound(voicePath,
                        new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                voicePlayPosition = -1;
                                holder.voice_anim.setBackgroundResource(R.mipmap.audio_animation_list_right_3);
                            }
                        });
            }

        });

        float voiceTime = 0;
        if (!TextUtils.isEmpty(chatBean.getStrVoiceTime())) {
            voiceTime = Float.parseFloat(chatBean.getStrVoiceTime());
        }
        ViewGroup.LayoutParams lParams = holder.voice_group.getLayoutParams();
        if (voiceTime < 5) {
            lParams.width = ScreenUtil.dip2px(context, 80);
        } else if (voiceTime < 10) {
            lParams.width = ScreenUtil.dip2px(context, 120);
        } else {
            lParams.width = ScreenUtil.dip2px(context, 160);
        }
        holder.voice_group.setLayoutParams(lParams);
        holder.voice_time.setText(chatBean.getStrVoiceTime() + "\"");
    }

    private void receiveVoiceUserLayout(final ToUserVoiceViewHolder holder, final ChatMessageBean chatBean, final int position) {
        /* time */
        long msgCreateTime = chatBean.getCreateTime();
        long currentTime = System.currentTimeMillis();
        if (TimeUtils.isSameYear(msgCreateTime)) {
            if (TextUtils.equals(TimeUtils.getDatas(msgCreateTime), TimeUtils.getDatas(currentTime))) {
                holder.chat_time.setText(TimeUtils.getDate(msgCreateTime));
            } else {
                holder.chat_time.setText(TimeUtils.geTimeNoS(msgCreateTime));
            }
        } else {
            holder.chat_time.setText(TimeUtils.getTime(msgCreateTime));
        }
        //holder.chat_time.setVisibility(View.VISIBLE);

        holder.othername.setText(toUser);
        holder.othertime.setText(TimeUtils.geTimeNoS(chatBean.getCreateTime()));

        holder.voice_group.setVisibility(View.VISIBLE);
        //根据状态是否显示 未读 小红点
        if (holder.receiver_voice_unread != null)
            holder.receiver_voice_unread.setVisibility(View.GONE);
        if (holder.receiver_voice_unread != null && unReadPosition != null) {
            for (String unRead : unReadPosition) {
                if (unRead.equals(position + "")) {
                    holder.receiver_voice_unread.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
        AnimationDrawable drawable;
        if (position == voicePlayPosition) {
            holder.voice_anim.setBackgroundResource(R.mipmap.audio_animation_list_left_3);
            holder.voice_anim.setBackgroundResource(R.drawable.voice_play_receiver);
            drawable = (AnimationDrawable) holder.voice_anim.getBackground();
            drawable.start();
        } else {
            holder.voice_anim.setBackgroundResource(R.mipmap.audio_animation_list_left_3);
        }
        holder.voice_anim.setId(position);
        holder.voice_group.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (holder.receiver_voice_unread != null)
                    holder.receiver_voice_unread.setVisibility(View.GONE);
                holder.voice_anim
                        .setBackgroundResource(R.mipmap.audio_animation_list_left_3);
                stopPlayVoice();
                voicePlayPosition = holder.voice_anim.getId();
                AnimationDrawable drawable;
                holder.voice_anim
                        .setBackgroundResource(R.drawable.voice_play_receiver);
                drawable = (AnimationDrawable) holder.voice_anim
                        .getBackground();
                drawable.start();
                String voicePath = chatBean.getContent().startsWith("http://") ?
                        chatBean.getContent() : "http://" + chatBean.getContent();
                if (voiceIsRead != null) {
                    voiceIsRead.voiceOnClick(position);
                }
                MediaManager.playSound(voicePath,
                        new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                voicePlayPosition = -1;
                                holder.voice_anim
                                        .setBackgroundResource(R.mipmap.audio_animation_list_left_3);
                            }
                        });
            }

        });

        float voiceTime = 0;
        if (!TextUtils.isEmpty(chatBean.getStrVoiceTime())){
            voiceTime = Float.parseFloat(chatBean.getStrVoiceTime());
        }
        ViewGroup.LayoutParams lParams = holder.voice_group.getLayoutParams();
        if (voiceTime < 5) {
            lParams.width = ScreenUtil.dip2px(context, 80);
        } else if (voiceTime < 10) {
            lParams.width = ScreenUtil.dip2px(context, 120);
        } else {
            lParams.width = ScreenUtil.dip2px(context, 160);
        }
        holder.voice_group.setLayoutParams(lParams);
        holder.voice_time.setText(chatBean.getStrVoiceTime() + "\"");
    }

    public class FromUserMsgViewHolder {
        public TextView chat_time;
        public CircleImageView headicon;
        public TextView myname, mytime;
        public TextView msg_content;
        public ImageView sendFailImg;  //是否发送失败
    }

    public class FromUserImageViewHolder {
        public TextView chat_time;
        public CircleImageView headicon;
        public TextView myname, mytime;
        public BubbleImageView msg_image;
        public ImageView sendFailImg; //是否发送失败
    }

    public class FromUserVoiceViewHolder {
        public TextView chat_time;
        public CircleImageView headicon;
        public TextView myname, mytime;
        public LinearLayout voice_group;
        public TextView voice_time;
        public View voice_anim;   //动画效果
        public ImageView sendFailImg; //是否发送失败
    }

    public class ToUserMsgViewHolder {
        public TextView chat_time;
        public CircleImageView headicon;
        public TextView othername, othertime;
        public TextView msg_content;
    }

    public class ToUserImgViewHolder {
        public TextView chat_time;
        public CircleImageView headicon;
        public TextView othername, othertime;
        public BubbleImageView msg_image;
    }

    public class ToUserVoiceViewHolder {
        public TextView chat_time;
        public CircleImageView headicon;
        public TextView othername, othertime;
        public LinearLayout voice_group;
        public TextView voice_time;
        public View voice_anim;
        public View receiver_voice_unread;  //语音消息是否读取了
    }
}
