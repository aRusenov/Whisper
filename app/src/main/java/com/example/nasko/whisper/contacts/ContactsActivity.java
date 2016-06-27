package com.example.nasko.whisper.contacts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nasko.whisper.Chat;
import com.example.nasko.whisper.User;
import com.example.nasko.whisper.chat.ChatActivity;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.data.ContactsData;
import com.example.nasko.whisper.data.ContactsEventListener;
import com.example.nasko.whisper.data.UserData;
import com.example.nasko.whisper.data.nodejs.NodeJsService;

public class ContactsActivity extends AppCompatActivity {

    private User currentUser;
    private ContactsData contactsData;
    private ArrayAdapter<Chat> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        this.currentUser = NodeJsService.getInstance().getUserData().getCurrentUser();

        ListView contactsView = (ListView) this.findViewById(R.id.contactsView);
        this.adapter = new ArrayAdapter<Chat>(this, R.layout.chat_item_layout) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Chat chat = getItem(position);

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_item_layout, parent, false);
                }

                TextView tvName = (TextView) convertView.findViewById(R.id.contact_name);
                TextView tvLastMessage = (TextView) convertView.findViewById(R.id.last_message);
                tvName.setText(chat.getLastMessage().getFrom());
                tvLastMessage.setText(chat.getLastMessage().getText());

                return convertView;
            }
        };
        contactsView.setAdapter(adapter);

        this.contactsData = NodeJsService.getInstance().getContactsData();
        this.contactsData.setContactsEventListener(new ContactsEventListener() {
            @Override
            public void onContactAdded(Chat chat) {
                adapter.add(chat);
            }

            @Override
            public void onContactUpdated(Chat chat) {
                adapter.remove(chat);
                adapter.insert(chat, 0);
            }
        });

        this.contactsData.getContacts(this.currentUser.getUsername());

        contactsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chat chat = adapter.getItem(position);
                Intent intent = new Intent(ContactsActivity.this, ChatActivity.class);
                intent.putExtra("chatId", chat.getId());
                intent.putExtra("lastMessageSeq", chat.getLastMessage().getSeq());

                ContactsActivity.this.startActivity(intent);
            }
        });

        Button button = (Button) this.findViewById(R.id.btn_create_chat);
        final EditText nameEdit = (EditText)this.findViewById(R.id.edit_name);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactName = nameEdit.getText().toString();
                contactsData.addContact(currentUser.getUsername(), contactName);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.adapter.clear();
        this.contactsData.getContacts(this.currentUser.getUsername());
    }
}
