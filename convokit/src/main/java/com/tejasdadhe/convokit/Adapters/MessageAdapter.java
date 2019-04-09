package com.tejasdadhe.convokit.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.tejasdadhe.convokit.Interfaces.ConversationPageEventsListener;
import com.tejasdadhe.convokit.Modals.Message;
import com.tejasdadhe.convokit.R;

import java.util.ArrayList;
import static com.tejasdadhe.convokit.Modals.Message.*;



public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private static final int VIEW_TYPE_SENT_MESSAGE = 1;
    private static final int VIEW_TYPE_RECIEVED_MESSAGE = 2;
    private static String TAG = " Message Adapter ";




    private ArrayList<Message> messageList , selectedMessageList;


    private ConversationPageEventsListener eventNotifier;

    private Context context;



    static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout messageBubble, messageAttachmentHolder, messageAudioHolder, quotedMessageHolder;
        TextView messageText, messageAuthor ;
        ImageView messageImage, messageStatus;
        ConstraintLayout messageMediaHolder;




        MyViewHolder(LinearLayout v) {
            super(v);
            messageBubble           = v;
            messageText             = messageBubble.findViewById(R.id.message_text);
            messageAuthor           = messageBubble.findViewById(R.id.author_name);
            messageStatus           = messageBubble.findViewById(R.id.mesage_status);
            messageImage            = messageBubble.findViewById(R.id.media_thumbnail);
            messageAudioHolder      = messageBubble.findViewById(R.id.audio_message_holder);
            messageAttachmentHolder = messageBubble.findViewById(R.id.attachment_holder);



            messageMediaHolder      = messageBubble.findViewById(R.id.media_message_holder);
            quotedMessageHolder     = messageBubble.findViewById(R.id.quoted_message_container);
        }
    }


    public MessageAdapter(@NonNull ArrayList<Message> messageList,Context activityContext) {
        this.messageList     = messageList;
        context              = activityContext;
        selectedMessageList  = new ArrayList<>();
    }



    public void addConverwsationPageEventListener(ConversationPageEventsListener eventNotifier)
    {
        this.eventNotifier = eventNotifier;
    }


    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout v;
        if(viewType == VIEW_TYPE_RECIEVED_MESSAGE)
            v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.recieved_message_item, parent, false);
        else
            v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_message_item, parent, false);
        return new MyViewHolder(v);
    }


    @Override
    public int getItemCount() {
        return this.messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(messageList.get(position).getMessageType() == Message.MESSAGE_TYPE_SENT_MESSAGE)
            return VIEW_TYPE_SENT_MESSAGE;
        else
            return VIEW_TYPE_RECIEVED_MESSAGE;
    }





    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final Message message = messageList.get(position);
        final int index = position;
        final View messageView = holder.messageBubble;
        final TextView messageAuthorName = messageView.findViewById(R.id.author_name);


        if(!selectedMessageList.isEmpty())
        {
            if(selectedMessageList.contains(message))
            {
                int color = ResourcesCompat.getColor(context.getResources(), R.color.CONVOKIT_SELECTED_MESSAGE_BACKGROUND, null);
                messageView.setBackgroundColor(color);
            }
            else
            {
                int color = Color.parseColor("#00000000");
                messageView.setBackgroundColor(color);
            }
        }
        else
        {
            int color = Color.parseColor("#00000000");
            messageView.setBackgroundColor(color);
        }


        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eventNotifier != null)
                {
                    if(selectedMessageList.contains(message))
                    {
                        selectedMessageList.remove(message);
                        eventNotifier.OnMessageUnselected(message,messageView,index,selectedMessageList.toArray(new Message[0]));
                    }
                    else
                    {
                        if (!selectedMessageList.isEmpty()) {
                            selectedMessageList.add(message);
                            eventNotifier.OnMessageSelected(message,messageView,index,selectedMessageList.toArray(new Message[0]));
                        }
                        eventNotifier.OnMessageClick(message, messageView, index);
                    }
                }
            }
        });

        messageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(eventNotifier != null)
                {

                    Log.d(TAG,"Message Selected ");
                    selectedMessageList.add(message);
                    eventNotifier.OnMessageLongClick(message,messageView,index);
                    eventNotifier.OnMessageSelected(message,messageView,index,selectedMessageList.toArray(new Message[0]));
                }
                return true;
            }
        });

        //Event handeling ends here


        if(message.getAuthorName() != null)
        {
            messageAuthorName.setVisibility(View.VISIBLE);
            messageAuthorName.setText(message.getAuthorName());
        }
        else
        {
            messageAuthorName.setVisibility(View.GONE);
        }

        if(message.getMessageType() == Message.MESSAGE_TYPE_SENT_MESSAGE)
        {
            switch (message.getMessageStatus())
            {
                case MESSAGE_STATUS_PENDING:
                {
                    //holder.messageStatus.setText("pending");
                    Glide.with(context)
                            .load(R.drawable.message_pending_icon)
                            .into(holder.messageStatus);
                    break;
                }
                case MESSAGE_STATUS_SENT:
                {
                    //holder.messageStatus.setText("sent");
                    Glide.with(context)
                            .load(R.drawable.message_sent_icon)
                            .into(holder.messageStatus);
                    break;
                }
                case MESSAGE_STATUS_RECEIVED:
                {
                    //holder.messageStatus.setText("recieved");
                    Glide.with(context)
                            .load(R.drawable.message_recieved_icon)
                            .into(holder.messageStatus);
                    break;
                }
                case MESSAGE_STATUS_READ:
                {
//                    holder.messageStatus.setText("read");
                    Glide.with(context)
                            .load(R.drawable.message_recieved_icon)
                            .into(holder.messageStatus);
                    break;
                }

                case MESSAGE_STATUS_SENDING_FAILED:
                {
//                    holder.messageStatus.setText("failed");
                    Glide.with(context)
                            .load(R.drawable.error_icon)
                            .into(holder.messageStatus);
                    break;
                }
                default:
                {
                    holder.messageStatus.setVisibility(View.GONE);
                }

            }
        }
        else
        {
            holder.messageStatus.setVisibility(View.GONE);
        }


        switch (message.getMessageFormat())
        {
            case MESSAGE_FORMAT_TEXT:
            {
                if(message.getMessageText() != null)
                {
                    holder.messageText.setVisibility(View.VISIBLE);
                    holder.messageText.setText(message.getMessageText());
                }
                else
                {
                    holder.messageText.setVisibility(View.GONE);
                }
                holder.messageBubble.setVisibility(View.VISIBLE);
                holder.messageMediaHolder.setVisibility(View.GONE);
                holder.messageAudioHolder.setVisibility(View.GONE);

                break;
            }
            case MESSAGE_FORMAT_IMAGE:
            {
                holder.messageMediaHolder.setVisibility(View.VISIBLE);
                holder.messageImage.setVisibility(View.VISIBLE);
                holder.messageAudioHolder.setVisibility(View.GONE);


                if(message.getMediaUrl() != null)
                {
                    Glide.with(context)
                            .load(message.getMediaUrl())
                            .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(30)))
                            .into(holder.messageImage);
                    if(message.getMessageStatus() == MESSAGE_STATUS_PENDING)
                    {
                        holder.messageMediaHolder.findViewById(R.id.media_progress_indicator).setVisibility(View.VISIBLE);
                    }
                    else if(message.getMessageStatus() == MESSAGE_STATUS_SENDING_FAILED)
                    {
                        holder.messageMediaHolder.findViewById(R.id.upload_button).setVisibility(View.VISIBLE);
                        holder.messageMediaHolder.findViewById(R.id.media_progress_indicator).setVisibility(View.GONE);
                    }
                }
                else
                    holder.messageImage.setVisibility(View.GONE);

                if(message.getMessageText() != null) {
                    holder.messageText.setVisibility(View.VISIBLE);
                    holder.messageText.setText(message.getMessageText());
                }
                else holder.messageText.setVisibility(View.GONE);


                break;
            }
            case MESSAGE_FORMAT_VIDEO:
            {

                holder.messageMediaHolder.setVisibility(View.VISIBLE);
                holder.messageImage.setVisibility(View.VISIBLE);


                if(message.getMessageText() != null)
                {
                    holder.messageText.setVisibility(View.VISIBLE);
                    holder.messageText.setText(message.getMessageText());
                }
                else
                {
                    holder.messageText.setVisibility(View.GONE);
                }


                final ImageButton downloadButton = holder.messageBubble.findViewById(R.id.download_button);
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        downloadButton.setVisibility(View.GONE);

                        final ProgressBar downloadLoader = holder.messageBubble.findViewById(R.id.media_progress_indicator);
                        final ImageButton cancelDownloadButton = holder.messageBubble.findViewById(R.id.cancel_download_button);

                        downloadLoader.setVisibility(View.VISIBLE);
                        cancelDownloadButton.setVisibility(View.VISIBLE);
                        cancelDownloadButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG,"canceled");
                                downloadLoader.setVisibility(View.GONE);
                                cancelDownloadButton.setVisibility(View.GONE);
                                downloadButton.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });



                holder.messageAudioHolder.setVisibility(View.GONE);
                break;
            }
            case MESSAGE_FORMAT_AUDIO:
            {
                holder.messageMediaHolder.setVisibility(View.GONE);

                if(message.getMessageText() != null)
                {
                    holder.messageText.setVisibility(View.VISIBLE);
                    holder.messageText.setText(message.getMessageText());
                }
                else
                {
                    holder.messageText.setVisibility(View.GONE);
                }

                holder.messageAttachmentHolder.setVisibility(View.VISIBLE);
                holder.messageAudioHolder.setVisibility(View.VISIBLE);


                final ProgressBar audioLoader = holder.messageBubble.findViewById(R.id.audio_loader);
                final ImageButton downloadAudioButton, cancelDownloadAudioButton, playAudioButton, pauseAudioButton;
                downloadAudioButton       = holder.messageBubble.findViewById(R.id.audio_download_button);
                cancelDownloadAudioButton = holder.messageBubble.findViewById(R.id.cancel_audio_download_button);
                playAudioButton           = holder.messageBubble.findViewById(R.id.audio_play_button);
                pauseAudioButton          = holder.messageBubble.findViewById(R.id.audio_pause_button);


                downloadAudioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setVisibility(View.GONE);
                        cancelDownloadAudioButton.setVisibility(View.VISIBLE);
                        audioLoader.setVisibility(View.VISIBLE);

                    }
                });

                playAudioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setVisibility(View.GONE);
                        pauseAudioButton.setVisibility(View.VISIBLE);
                    }
                });

                pauseAudioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setVisibility(View.GONE);
                        playAudioButton.setVisibility(View.VISIBLE);
                    }
                });

                cancelDownloadAudioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setVisibility(View.GONE);
                        audioLoader.setVisibility(View.GONE);
                        downloadAudioButton.setVisibility(View.VISIBLE);

                    }
                });
                break;
            }
            case MESSAGE_FORMAT_FILE:
            {
                holder.messageAttachmentHolder.setVisibility(View.VISIBLE);
                holder.messageAudioHolder.setVisibility(View.GONE);
                break;
            }
        }


        if(message.getQuotedMessage() != null)
        {
            final Message quotedMessage = message.getQuotedMessage();

            TextView quotedMessageAuthorName;
            View quotedMessageView = holder.quotedMessageHolder;


            switch (quotedMessage.getMessageFormat())
            {
                case MESSAGE_FORMAT_TEXT :
                {
                    quotedMessageAuthorName = holder.quotedMessageHolder.findViewById(R.id.quoted_text_message_author);
                    setQuotedMessageViews(quotedMessageView,View.VISIBLE,View.GONE,View.GONE,View.GONE);
                    if(quotedMessage.getMessageText() != null)
                    {
                        TextView quotedMessageText =  holder.quotedMessageHolder.findViewById(R.id.quoted_message_text_view);
                        quotedMessageText.setVisibility(View.VISIBLE);
                        quotedMessageText.setText(quotedMessage.getMessageText());
                    }
                    break;
                }
                case MESSAGE_FORMAT_IMAGE :
                {
                    quotedMessageAuthorName = holder.quotedMessageHolder.findViewById(R.id.quoted_media_message_author);
                    setQuotedMessageViews(quotedMessageView,View.GONE,View.GONE,View.VISIBLE,View.GONE);
                    break;
                }
                case MESSAGE_FORMAT_AUDIO :
                {
                    quotedMessageAuthorName = holder.quotedMessageHolder.findViewById(R.id.quoted_audio_message_author);
                    setQuotedMessageViews(quotedMessageView,View.GONE,View.VISIBLE,View.GONE,View.GONE);
                    break;
                }
                case MESSAGE_FORMAT_VIDEO :
                {
                    quotedMessageAuthorName = holder.quotedMessageHolder.findViewById(R.id.quoted_media_message_author);
                    setQuotedMessageViews(quotedMessageView,View.GONE,View.GONE,View.VISIBLE,View.GONE);
                    break;
                }
                case MESSAGE_FORMAT_FILE :
                {
                    quotedMessageAuthorName = holder.quotedMessageHolder.findViewById(R.id.quoted_attachment_message_author);
                    setQuotedMessageViews(quotedMessageView,View.GONE,View.GONE,View.GONE,View.VISIBLE);
                    break;
                }
                default:
                {
                    quotedMessageAuthorName = quotedMessageView.findViewById(R.id.quoted_text_message_author);
                    setQuotedMessageViews(quotedMessageView,View.GONE,View.GONE,View.GONE,View.GONE);
                    break;
                }
            }

            if(quotedMessage.getAuthorName() != null)
            {
                quotedMessageAuthorName.setVisibility(View.VISIBLE);
                quotedMessageAuthorName.setText(quotedMessage.getAuthorName());
            }
            else
            {
                quotedMessageAuthorName.setVisibility(View.GONE);
            }

            holder.quotedMessageHolder.setVisibility(View.VISIBLE);
            holder.quotedMessageHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventNotifier.OnQuotedMessageClick(quotedMessage,message,holder.messageBubble,holder.quotedMessageHolder,position);
                }
            });
            Log.d(TAG," Quoted message found ");
        }
        else
        {
            holder.quotedMessageHolder.setVisibility(View.GONE);
        }
    }


    private void setQuotedMessageViews(View quotedMessageView,int text_message_visibility,int audio_message_visibility,int media_message_visibility,int attachment_message_visibility)
    {
        quotedMessageView.findViewById(R.id.quoted_text_message).setVisibility(text_message_visibility);
        quotedMessageView.findViewById(R.id.quoted_audio_message).setVisibility(audio_message_visibility);
        quotedMessageView.findViewById(R.id.quoted_media_message).setVisibility(media_message_visibility);
        quotedMessageView.findViewById(R.id.quoted_attachment_message).setVisibility(attachment_message_visibility);
    }

    public void clearSelectedMessages()
    {
        int count = selectedMessageList.size();
        selectedMessageList.clear();
        eventNotifier.OnSelectedMessagesCleared(count);
    }

    public Message[] getSelectedMessages()
    {
        return selectedMessageList.toArray(new Message[0]);
    }

    public int getSelectedMessageCount()
    {
        return selectedMessageList.size();
    }

}