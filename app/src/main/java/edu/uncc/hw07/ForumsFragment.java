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
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

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
                        mForums.clear();
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
                mBinding.textViewForumLikesDate.setText(mForum.getLikes() + " Likes | " + mForum.createdAt);

                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delete(mForum);
                    }
                });

                // Get the user id
                mAuth = FirebaseAuth.getInstance();
                String user_id = mAuth.getCurrentUser().getUid();

                // If the forum is created by the user, make delete button visible
                if (user_id.equals(mForum.creator_id)) {
                    mBinding.imageViewDelete.setVisibility(View.VISIBLE);
                } else {
                    mBinding.imageViewDelete.setVisibility(View.INVISIBLE);
                }

                // This sets the initial like image
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference collForums = db.collection("forums");
                CollectionReference collLikes = collForums.document(mForum.forum_id).collection("likes");
                setImage();

                // Like Button
                mBinding.imageViewLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: ");
                        // Check Database if the user's name is in the list or not
                        collLikes.get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        boolean userInList = false;
                                        for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                                            mAuth = FirebaseAuth.getInstance();
                                            Log.d(TAG, "onSuccess: " + mAuth.getCurrentUser().getUid());
                                            Log.d(TAG, "onSuccess: " + document.getString("user_id"));
                                            if (document.getString("user_id").equals(mAuth.getCurrentUser().getUid())){
                                               updateData(true);
                                               userInList = true;
                                            }
                                        }
                                        if (!userInList) {
                                            updateData(false);
                                        }
                                    }
                                });
                    }
                });
            }

            /**
             * This updates the data depending on whether the user is in the list or not
             * @param userInList
             */
            private void updateData(boolean userInList) {
                // This sets the initial like image
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference collForums = db.collection("forums");
                CollectionReference collLikes = collForums.document(mForum.forum_id).collection("likes");

                if (userInList) {
                    collLikes.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    // Loop and delete if it exists
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        mAuth = FirebaseAuth.getInstance();
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // If dummy document take care of it
                                                if (document.getString("user_id").equals(mAuth.getCurrentUser().getUid())) {
                                                    Log.d(TAG, "Delete Like");
                                                    // In the case where this is the only like left, you must create a dummy file
                                                    // Otherwise the collection will disappear and break the database
                                                    if (mForum.likes == 1) {
                                                        HashMap<String, Object> like = new HashMap<>();
                                                        like.put("user_id", "dummy");

                                                        // Create dummy file
                                                        String like_id = collLikes.document("dummy").getId();
                                                        collLikes.document(like_id).set(like);
                                                    }
                                                    // Delete user_id from list
                                                    collLikes.document(document.getId()).delete();
                                                    // Decrement likes
                                                    mForum.likes--;
                                                    // Update with new number of likes
                                                    collForums.document(mForum.forum_id).update("likes", mForum.likes);
                                                    // Set the new image
                                                    setImage();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                } else {
                    // Like a Forum
                    collLikes.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    // If this is the first Like
                                    if (mForum.likes == 0) {
                                        Log.d(TAG, "First Like");
                                        HashMap<String, Object> like = new HashMap<>();
                                        like.put("user_id", mAuth.getCurrentUser().getUid());

                                        String like_id = collLikes.document().getId();
                                        collLikes.document(like_id).set(like);

                                        // Delete dummy file
                                        collLikes.document("dummy").delete();
                                    } else {
                                        // Add like
                                        Log.d(TAG, "Like");
                                        HashMap<String, Object> like = new HashMap<>();
                                        like.put("user_id", mAuth.getCurrentUser().getUid());

                                        String like_id = collLikes.document().getId();
                                        collLikes.document(like_id).set(like);

                                    }
                                    // Increment likes
                                    mForum.likes++;
                                    // Update likes
                                    collForums.document(mForum.forum_id).update("likes", mForum.likes);
                                    // Set new image
                                    setImage();
                                }
                            });
                }
            }

            /**
             * This method sets the image to Like or Not Like depending on if the user's id is in
             * the list of liked users
             */
            private void setImage() {
                // This sets the initial like image
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference collForums = db.collection("forums");
                CollectionReference collLikes = collForums.document(mForum.forum_id).collection("likes");

                // ImageView
                ImageView img = mBinding.imageViewLike;

                // Set to Not Like by default
                img.setImageResource(R.drawable.like_not_favorite);

                // Check if it should be liked and change the image if needed
                collLikes.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot document: queryDocumentSnapshots) {
                                    if((mAuth.getCurrentUser().getUid()).equals(document.getString("user_id"))) {
                                        img.setImageResource(R.drawable.like_favorite);
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: ");
                            }
                        });
            }
        }
    }

    private void delete(Forum forum) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("forums").document(forum.forum_id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: ");
                    }
                });
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