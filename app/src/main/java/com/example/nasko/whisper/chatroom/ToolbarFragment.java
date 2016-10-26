package com.example.nasko.whisper.chatroom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.chatroom.di.modules.ChatroomToolbarPresenterModule;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ToolbarFragment extends Fragment implements ToolbarContract.View {

    public static final String EXTRA_CHAT = "chat";

    @Inject ToolbarContract.Presenter presenter;
    private ContactViewModel displayContact;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.image_contact) CircleImageView imageContact;
    @BindView(R.id.tv_contact_name) TextView tvName;
    @BindView(R.id.tv_status) TextView tvStatus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatViewModel chat = getArguments().getParcelable(EXTRA_CHAT);
        if (chat != null) {
            displayContact = chat.getDisplayContact();
        }

        WhisperApplication.userComponent()
                .plus(new ChatroomToolbarPresenterModule(this))
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatroom_toolbar, container, false);
        ButterKnife.bind(this, view);

        if (displayContact == null) {
            toolbar.setVisibility(View.GONE);
        } else {
            AppCompatActivity host = (AppCompatActivity) getActivity();
            host.setSupportActionBar(toolbar);
            host.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME);

            TypedValue tv = new TypedValue();
            if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            {
                int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());

                imageContact.getLayoutParams().width = (actionBarHeight * 3) / 4;
                imageContact.getLayoutParams().height = (actionBarHeight * 3) / 4;
                Picasso.with(getContext())
                        .load(displayContact.getImage().getUrl())
                        .placeholder(R.drawable.profile)
                        .into(imageContact);

                tvName.setText(displayContact.getUsername());
                tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, actionBarHeight / 3);
                tvStatus.setTextSize(TypedValue.COMPLEX_UNIT_PX, actionBarHeight / 4);
                setContactStatus(displayContact.isOnline());
            }
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    @Override
    public void setContactStatus(boolean online) {
        tvStatus.setText(online ? getActivity().getString(R.string.status_online) : getActivity().getString(R.string.status_offline));
    }
}
