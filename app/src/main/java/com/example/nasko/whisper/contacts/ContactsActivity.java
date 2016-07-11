package com.example.nasko.whisper.contacts;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nasko.whisper.Chat;
import com.example.nasko.whisper.Contact;
import com.example.nasko.whisper.DateProvider;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.User;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.chat.ChatActivity;
import com.example.nasko.whisper.data.ContactsData;
import com.example.nasko.whisper.data.Error;
import com.example.nasko.whisper.data.LocalUserRepository;
import com.example.nasko.whisper.data.UserData;
import com.example.nasko.whisper.data.listeners.OnErrorListener;
import com.example.nasko.whisper.data.listeners.OnSuccessListener;
import com.example.nasko.whisper.data.nodejs.NodeJsService;
import com.example.nasko.whisper.login.LoginActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ContactsActivity extends AppCompatActivity implements DateProvider {

    public static final String KEY_CHAT_ID = "chatId";
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private User currentUser;
    private ContactsData contactsData;
    private ChatAdapter adapter;
    private Date today = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setBackground(new ColorDrawable(Color.parseColor("#26A69A")));
        setSupportActionBar(myToolbar);

        UserData userData = WhisperApplication.getInstance().getChatService().getUserData();
        this.currentUser = userData.getCurrentUser();
        this.contactsData = NodeJsService.getInstance().getContactsData();

        RecyclerView contactsView = (RecyclerView) this.findViewById(R.id.contactsView);
        this.adapter = new ChatAdapter(this, this);
        this.contactsData.setContactsEventListener(this.adapter);
        this.adapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Chat selectedChat = adapter.getItem(position);

                Intent intent = new Intent(ContactsActivity.this, ChatActivity.class);
                intent.putExtra(KEY_CHAT_ID, selectedChat.getId());
                // TODO: Remove
                intent.putExtra("lastMessageSeq", selectedChat.getLastMessage().getSeq());

                ContactsActivity.this.startActivity(intent);
            }
        });

        contactsView.setAdapter(this.adapter);
        contactsView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.today = new Date();
        this.adapter.clear();
        this.contactsData.getContacts(this.currentUser.getUsername());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.bar_actions, menu);;

        MenuItem searchItem = menu.findItem(R.id.action_add_contact);
        final SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        final RecyclerView contactSearchView = (RecyclerView) findViewById(R.id.new_contacts_view);
        final ContactQueryAdapter adapter = new ContactQueryAdapter(this, currentUser);
        contactSearchView.setAdapter(adapter);
        contactSearchView.setLayoutManager(new LinearLayoutManager(this));

        final TextView tvQueryMessage = (TextView) findViewById(R.id.query_message);
        final RelativeLayout queryContainer = (RelativeLayout) findViewById(R.id.new_contacts_container);

        searchView.setQueryHint(getString(R.string.add_contact_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() <= 1) {
                    return true;
                }

                contactsData.queryContacts(query, new OnSuccessListener<List<Contact>>() {
                    @Override
                    public void onSuccess(List<Contact> contacts) {
                        if (contacts.size() == 0) {
                            tvQueryMessage.setVisibility(View.VISIBLE);
                            tvQueryMessage.setText("No results");
                        } else {
                            tvQueryMessage.setVisibility(View.GONE);
                        }

                        adapter.clear();
                        adapter.addAll(contacts);
                    }
                }, new OnErrorListener<Error>() {
                    @Override
                    public void onError(Error error) {
                        Log.e("ERROR", error.toString());
                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
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
                adapter.clear();
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
                LocalUserRepository localUserRepository = new LocalUserRepository(this);
                localUserRepository.logout();

                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Date getDate() {
        return this.today;
    }
}
