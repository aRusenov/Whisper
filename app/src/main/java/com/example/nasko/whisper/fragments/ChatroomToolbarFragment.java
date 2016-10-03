package com.example.nasko.whisper.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.presenters.Navigator;
import com.example.nasko.whisper.presenters.chatroom.ChatroomToolbarPresenter;
import com.example.nasko.whisper.presenters.chatroom.ChatroomToolbarPresenterImpl;
import com.example.nasko.whisper.views.contracts.ChatroomNavBarView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatroomToolbarFragment extends BaseFragment<ChatroomToolbarPresenter> implements ChatroomNavBarView {

    private ContactViewModel displayContact;

    @BindView(R.id.toolbar_container) LinearLayout toolbarContainer;
    @BindView(R.id.image_contact) CircleImageView imageContact;
    @BindView(R.id.tv_contact_name) TextView tvName;
    @BindView(R.id.tv_status) TextView tvStatus;
    @BindView(R.id.status_image) ImageView statusImg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatViewModel chat = getArguments().getParcelable(Navigator.EXTRA_CHAT);
        if (chat != null) {
            displayContact = chat.getDisplayContact();
        }

        setPresenter(new ChatroomToolbarPresenterImpl());
        getPresenter().attachView(this, getContext(), null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatroom_toolbar, container, false);
        ButterKnife.bind(this, view);

        if (displayContact == null) {
            toolbarContainer.setVisibility(View.GONE);
        } else {
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
    public void setContactStatus(boolean online) {
        int statusRes = online ? R.drawable.circle_green : R.drawable.circle_gray;
        statusImg.setImageResource(statusRes);
        tvStatus.setText(online ? getString(R.string.status_online) : getString(R.string.status_offline));
    }
}
