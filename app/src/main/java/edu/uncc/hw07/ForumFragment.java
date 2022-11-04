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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import edu.uncc.hw07.databinding.CommentRowItemBinding;
import edu.uncc.hw07.databinding.FragmentForumBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForumFragment extends Fragment {

    final String TAG = "test";
    private FirebaseAuth mAuth;
    FragmentForumBinding binding;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_FORUM = "forum";
    private Forum mForum;

    public ForumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mForum Parameter 1.
     * @return A new instance of fragment ForumFragment.
     */
    public static ForumFragment newInstance(Forum mForum) {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_FORUM, mForum);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mForum = (Forum) getArguments().getSerializable(ARG_PARAM_FORUM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentForumBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.textViewForumTitle.setText(mForum.title);
        binding.textViewForumCreatedBy.setText(mForum.creator_name);
        binding.textViewForumText.setText(mForum.description);

        getActivity().setTitle(R.string.forum);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CommentAdapter();
        binding.recyclerView.setAdapter(adapter);

        getComments();

        binding.buttonSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get text
                String text = binding.editTextComment.getText().toString();

                // Check text
                if(text.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a comment.", Toast.LENGTH_SHORT).show();
                } else {
                    String user_id = mAuth.getCurrentUser().getUid();
                    String user_name = mAuth.getCurrentUser().getDisplayName();
                    // Post Comment
                    postComment(text, user_id, user_name);
                }
            }
        });
    }

    private  void getComments() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("forums").document(mForum.forum_id).collection("comments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for(QueryDocumentSnapshot document : value) {
                            Comment comment = document.toObject(Comment.class);
                            mComments.add(comment);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void postComment(String text, String user_id, String user_name) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference collComments = db.collection("forums").document(mForum.forum_id).collection("comments");
        String comment_id = collComments.document().getId();

        // Get the current date and time
        // Format it to a String
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String created_At = df.format(Calendar.getInstance().getTime());

        // Create the comment
        HashMap<String, Object> comment = new HashMap<>();
        comment.put("text", text);
        comment.put("user_id", user_id);
        comment.put("user_name", user_name);
        comment.put("created_At", created_At);
        comment.put("comment_id", comment_id);

        collComments.document(comment_id).set(comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }

    CommentAdapter adapter;
    ArrayList<Comment> mComments;

    class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

        @NonNull
        @Override
        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CommentRowItemBinding binding = CommentRowItemBinding.inflate(getLayoutInflater(), parent, false);
            return new CommentViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
            Comment comment = mComments.get(position);
            holder.setupUI(comment);
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        class CommentViewHolder extends RecyclerView.ViewHolder {

            CommentRowItemBinding mBinding;
            Comment mComment;

            public CommentViewHolder(CommentRowItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            private void setupUI(Comment comment) {
                mComment = comment;
                mBinding.textViewCommentText.setText(mComment.text);
                mBinding.textViewCommentCreatedBy.setText(mComment.user_name);
                mBinding.textViewCommentCreatedAt.setText(mComment.created_At);

                // Delete Button visibility
                if (mAuth.getCurrentUser().getUid().equals(mComment.user_id)) {
                    mBinding.imageViewDelete.setVisibility(View.VISIBLE);
                } else {
                    mBinding.imageViewDelete.setVisibility(View.INVISIBLE);
                }

                // Delete Button
                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ForumFragmentListener) {
            mListener = (ForumFragmentListener) context;
        }
    }

    ForumFragmentListener mListener;

    public interface ForumFragmentListener {
    }
}