package edu.uncc.hw07;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import edu.uncc.hw07.databinding.ForumRowItemBinding;
import edu.uncc.hw07.databinding.FragmentForumsBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForumsFragment extends Fragment {

    FragmentForumsBinding binding;
    private FirebaseAuth mAuth;
    final String TAG = "test";

    public ForumsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentForumsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.forums);

        // Get Forums from FireStore
        getForums();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ForumAdapter();
        binding.recyclerView.setAdapter(adapter);

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.logout();
            }
        });

        binding.buttonCreateForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.newForum();
            }
        });
    }

    private void getForums() {
        // Database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Forums Collection
        db.collection("forums")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot document: value){
                            Log.d(TAG, "onEvent: " + document.getString("creator_name"));
                            Forum forum = document.toObject(Forum.class);
                            mForums.add(forum);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    ArrayList<Forum> mForums = new ArrayList<>();
    ForumAdapter adapter = new ForumAdapter();

    class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ForumViewHolder> {
        @NonNull
        @Override
        public ForumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ForumRowItemBinding binding = ForumRowItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ForumViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ForumViewHolder holder, int position) {
            Forum forum = mForums.get(position);
            holder.setupUI(forum);
        }

        @Override
        public int getItemCount() {
            return mForums.size();
        }

        class ForumViewHolder extends RecyclerView.ViewHolder {

            ForumRowItemBinding mBinding;
            Forum mForum;

            public ForumViewHolder(ForumRowItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(Forum forum) {
                mForum = forum;
                mBinding.textViewForumTitle.setText(mForum.title);
                mBinding.textViewForumCreatedBy.setText(mForum.creator_name);
                mBinding.textViewForumText.setText(mForum.description);
                // TODO Get date/time created
                mBinding.textViewForumLikesDate.setText(" Likes | " + mForum.createdAt);

                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO Delete Forum
                    }
                });

                mBinding.imageViewLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO Like/Unlike Forum
                    }
                });
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ForumsFragmentListener) {
            mListener = (ForumsFragmentListener) context;
        }
    }

    ForumsFragmentListener mListener;

    public interface ForumsFragmentListener{
        void logout();
        void newForum();
    }
}