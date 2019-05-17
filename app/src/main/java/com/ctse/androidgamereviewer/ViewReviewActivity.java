package com.ctse.androidgamereviewer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ctse.androidgamereviewer.data.entities.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewReviewActivity extends AppCompatActivity {

    public static final int EDIT_REVIEW_REQUEST = 11;

    private ReviewViewModel reviewViewModel;

    private TextView tvReviewTitle;
    private TextView tvReviewBody;
    private RatingBar ratingBar;
    private LinearLayout linearLayout;
    private Button buttonEditReivew;
    private Button buttonDeleteReview;

    FirebaseUser user;

    private String reviewId;
    private int reviewLocalId;
    private String userEmail;
    private String gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_review);

        reviewId = getIntent().getStringExtra(ReviewViewAdapter.EXTRA_REVIEW_ID);
        reviewLocalId = getIntent().getIntExtra(ReviewViewAdapter.EXTRA_REVIEW_LOCAL_ID, -1);
        userEmail = getIntent().getStringExtra(ReviewViewAdapter.EXTRA_REVIEW_USER_EMAIL);
        gameId = getIntent().getStringExtra(ReviewViewAdapter.EXTRA_REVIEW_GAME_ID);

        user = FirebaseAuth.getInstance().getCurrentUser();

        tvReviewTitle = findViewById(R.id.text_view_review_title_view);
        tvReviewBody = findViewById(R.id.text_view_review_body_view);
        ratingBar = findViewById(R.id.ratingBarReview);
        linearLayout = findViewById(R.id.layout_options);
        buttonEditReivew = findViewById(R.id.button_edit_review);
        buttonDeleteReview = findViewById(R.id.button_delete_review);

        reviewViewModel = ViewModelProviders.of(this).get(ReviewViewModel.class);
        reviewViewModel.getReviewById(reviewId).observe(this, new Observer<Review>() {
            @Override
            public void onChanged(Review review) {
                tvReviewBody.setText(review.getBody());
                tvReviewTitle.setText(review.getTitle());
                ratingBar.setRating(review.getRating());

                if (null != user && user.getEmail().equals(review.getUserEmail())) {
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    linearLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        buttonEditReivew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewReviewActivity.this, AddReviewActivity.class);
                intent.putExtra(ReviewViewAdapter.EXTRA_REVIEW_ID, reviewId);
                intent.putExtra(MainActivity.REVIEW_REQUEST_CODE, EDIT_REVIEW_REQUEST);
                startActivityForResult(intent, EDIT_REVIEW_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_REVIEW_REQUEST && resultCode == RESULT_OK) {
            int rating = data.getIntExtra(AddReviewActivity.EXTRA_REVIEW_RATING, 0);
            String reviewTitle = data.getStringExtra(AddReviewActivity.EXTRA_REVIEW_TITLE);
            String reviewBody = data.getStringExtra(AddReviewActivity.EXTRA_REVIEW_BODY);

            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Review review = new Review();

            review.setRating(rating);
            review.setTitle(reviewTitle);
            review.setBody(reviewBody);
            review.setDate(dateFormat.format(date.getTime()));
            review.setGameId(gameId);
            review.set_id(reviewId);
            review.setUserEmail(userEmail);
            review.setId(reviewLocalId);

            reviewViewModel.update(review);
//            reviewViewModel.insert(review);

            Toast.makeText(this, "Review Saved", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Review did not saved", Toast.LENGTH_SHORT).show();
        }
    }
}
