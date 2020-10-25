package com.example.przemek.gymdiary.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.przemek.gymdiary.Adapters.MessagesAdapter;
import com.example.przemek.gymdiary.DbManagement.ChatManagement;
import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Interfaces.ObserveCollectionInterface;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulChat;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulMessage;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulUser;
import com.example.przemek.gymdiary.Models.Message;
import com.example.przemek.gymdiary.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity {
    @BindView(R.id.chat_toolbar)
    Toolbar toolbar;
    @BindView(R.id.chat_button_send)
    ImageButton btnSend;
    @BindView(R.id.chat_edit_text)
    EditText etMessage;
    @BindView(R.id.chat_messages_list)
    RecyclerView listOfMessages;

    String chatId;
    ChatManagement chatManagement = new ChatManagement();
    HelpfulChat chat;
    HelpfulUser currentUser;
    MessagesAdapter mAdapter;

    ArrayList<HelpfulMessage> messages = new ArrayList<>();

    boolean isScrolling;
    boolean isLastItemReached;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        Intent i = getIntent();
        chatId = i.getStringExtra("chatId");
        currentUser = (HelpfulUser) i.getSerializableExtra("user");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fetchChat();


        btnSend.setOnClickListener(c -> {
            if (!TextUtils.isEmpty(etMessage.getText())) {
                btnSend.setEnabled(false);
                Message message = new Message();
                message.setWriter(currentUser);
                message.setMessage(etMessage.getText().toString());
                Helper.hideKeyboard(this);
                etMessage.setText("");
                scrollDown();
                chatManagement.sendMessage(chatId, message, status -> {
                    btnSend.setEnabled(true);

                    if (status == DbStatus.Success)
                        Toast.makeText(this, "Wysłałeś wiadomość", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(this, "Nie wysłałeś wiadomości", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private void fetchChat() {
        chatManagement.getChat(chatId, chat -> {
            if (chat != null) {
                this.chat = chat;
                chatManagement.getInitialChatMessages(chat.getChatId(), messages -> {
                    if (messages != null) {
                        Log.d("Chat", "ChatId: " + chat.getChatId() + " Wiadomości: " + String.valueOf(messages.size()));
                        this.messages = messages;
                        assignMessages();
                    }
                });
            }
        });
    }

    private void runObservators() {
        chatManagement.observeMessages(new ObserveCollectionInterface<HelpfulMessage>() {
            @Override
            public void onAdd(HelpfulMessage object) {
                addMessageToChat(object);
            }

            @Override
            public void onDelete(HelpfulMessage object) {
            }

            @Override
            public void onModify(HelpfulMessage object) {
            }
        }, this, chatId);
    }

    private void addMessageToChat(HelpfulMessage object) {
        if (!checkItemIsInArray(object)) {
            messages.add(0, object);
            mAdapter.notifyDataSetChanged();
        }

    }

    private boolean checkItemIsInArray(HelpfulMessage message) {

        for (int i = 0; i < messages.size(); i++) {
            HelpfulMessage u = messages.get(i);
            if (u.getId().equals(message.getId()))
                return true;
        }
        return false;
    }

    private void assignMessages() {

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setReverseLayout(true);
        mAdapter = new MessagesAdapter(this, messages);
        mAdapter.notifyDataSetChanged();
        listOfMessages.setLayoutManager(mLinearLayoutManager);
        listOfMessages.setAdapter(mAdapter);
        listOfMessages.postDelayed(() -> listOfMessages.smoothScrollToPosition(0), 100);
        runObservators();

        listOfMessages.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                if (messages.size() > 0 && messages != null)
                    scrollDown();
            }
        });


        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = mLinearLayoutManager.getChildCount();
                int totalItemCount = mLinearLayoutManager.getItemCount();

                if (isScrolling && (firstVisibleItem + visibleItemCount == totalItemCount) && !isLastItemReached) {
                    isScrolling = false;
                    chatManagement.getNextChatMessages(chatId, data -> {
                        messages.addAll(data);
                        mAdapter.notifyDataSetChanged();
                        if (data.size() < 5) {
                            isLastItemReached = true;
                        }
                    });
                }

            }
        };
        if (!messages.isEmpty())
            listOfMessages.addOnScrollListener(onScrollListener);

    }

    private void scrollDown() {
        listOfMessages.postDelayed(() -> listOfMessages.smoothScrollToPosition(0), 100);
    }

    @Override
    protected void onResume() {
        if (mAdapter != null)
            runObservators();
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
