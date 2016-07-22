package com.example.nasko.whisper.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Contact;
import com.example.nasko.whisper.presenters.ChatsPresenter;
import com.example.nasko.whisper.presenters.ChatsPresenterImpl;
import com.example.nasko.whisper.utils.DateProvider;
import com.example.nasko.whisper.views.adapters.ChatAdapter;
import com.example.nasko.whisper.views.adapters.ContactQueryAdapter;
import com.example.nasko.whisper.views.contracts.ChatsActionBarView;
import com.example.nasko.whisper.views.contracts.ChatsView;

import java.util.Date;
import java.util.List;

public class ChatsActivity extends AppCompatActivity implements ChatsView, ChatsActionBarView, DateProvider {

    private static final String TAG = "ChatsActivity";
    private static final String KEY_CHAT_ID = "chatId";

    private TextView tvStatus;
    private TextView tvQueryMessage;

    private ChatAdapter chatsAdapter;
    private ContactQueryAdapter contactQueryAdapter;
    private Date today = new Date();

    private ChatsPresenter chatsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Log.d(TAG, "Creating activity");

        chatsPresenter = new ChatsPresenterImpl(this);
        chatsPresenter.onTakeViews(this, this);

        RecyclerView contactsView = (RecyclerView) this.findViewById(R.id.chatsView);
        this.chatsAdapter = new ChatAdapter(this, this);
        this.chatsAdapter.setItemClickListener(position -> {
            Chat selectedChat = chatsAdapter.getItem(position);
            chatsPresenter.onChatClicked(selectedChat);
        });

        contactsView.setAdapter(this.chatsAdapter);
        contactsView.setLayoutManager(new LinearLayoutManager(this));
        displayToolbar();
    }

    private void displayToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setBackground(new ColorDrawable(Color.parseColor("#26A69A")));
        setSupportActionBar(myToolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resuming activity");
        // TODO: extract in presenter
        this.today = new Date();

        chatsPresenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroying activity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.bar_actions, menu);;

        tvStatus = new TextView(this);
        tvStatus.setVisibility(View.GONE);
        tvStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        MenuItem searchItem = menu.findItem(R.id.action_add_contact);
        final SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        final RecyclerView contactSearchView = (RecyclerView) findViewById(R.id.new_contacts_view);
        contactQueryAdapter = new ContactQueryAdapter(this, chatsPresenter.getCurrentUser());
        contactQueryAdapter.setInvitationIconClickListener(position -> {
            Contact contact = contactQueryAdapter.getItem(position);
            chatsPresenter.onContactSendRequestClick(contact);
        });

        contactSearchView.setAdapter(contactQueryAdapter);
        contactSearchView.setLayoutManager(new LinearLayoutManager(this));

        tvQueryMessage = (TextView) findViewById(R.id.query_message);
        final RelativeLayout queryContainer = (RelativeLayout) findViewById(R.id.new_contacts_container);

        searchView.setQueryHint(getString(R.string.add_contact_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                chatsPresenter.onQueryEntered(query);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                queryContainer.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                contactQueryAdapter.clear();
                queryContainer.setVisibility(View.GONE);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_contact:
                // TODO: Send request
                return true;
            case R.id.action_logout:
                chatsPresenter.onLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Date getDate() {
        return this.today;
    }

    @Override
    public void loadChats(List<Chat> chats) {
        chatsAdapter.addAll(chats);
    }

    @Override
    public void addChat(Chat chat) {
        chatsAdapter.add(chat);
    }

    @Override
    public void updateChat(Chat chat) {
        chatsAdapter.update(chat);
    }

    @Override
    public void clearChats() {
        chatsAdapter.clear();
    }

    @Override
    public void navigateToChatroom(Chat chat) {
        Intent intent = new Intent(ChatsActivity.this, ChatroomActivity.class);
        intent.putExtra(KEY_CHAT_ID, chat.getId());
        // TODO: Remove
        intent.putExtra("lastMessageSeq", chat.getLastMessage().getSeq());

        ChatsActivity.this.startActivity(intent);
    }

    @Override
    public void loadQueryResults(List<Contact> contacts) {
        if (contacts.size() == 0) {
            tvQueryMessage.setVisibility(View.VISIBLE);
            tvQueryMessage.setText("No results");
        } else {
            tvQueryMessage.setVisibility(View.GONE);
        }

        contactQueryAdapter.clear();
        contactQueryAdapter.addAll(contacts);
    }

    @Override
    public void displayInfoText(String text) {

    }

    @Override
    public void navigateToLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
