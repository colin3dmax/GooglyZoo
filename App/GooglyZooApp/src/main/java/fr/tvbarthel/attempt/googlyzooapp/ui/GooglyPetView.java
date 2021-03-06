package fr.tvbarthel.attempt.googlyzooapp.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import fr.tvbarthel.attempt.googlyzooapp.R;
import fr.tvbarthel.attempt.googlyzooapp.listener.GooglyPetListener;
import fr.tvbarthel.attempt.googlyzooapp.model.GooglyEye;
import fr.tvbarthel.attempt.googlyzooapp.model.GooglyPet;

/**
 * Created by tbarthel on 06/02/14.
 */
public class GooglyPetView extends ImageView {

    /**
     * Log cat
     */
    private static final String TAG = GooglyPetView.class.getName();

    /**
     * pupil radius
     */
    private float mPupilRadius;

    /**
     * Paint used to draw eyes
     */
    private Paint mPaint;

    /**
     * View model
     */
    private GooglyPet mGooglyPetModel;

    /**
     * use to place pet well when displayed for the first time
     * image view height is used for this placement and is available starting from the onLayout
     */
    private boolean mFirstDisplay;


    public GooglyPetView(Context context, GooglyPet model) {
        super(context);

        //init paint
        mPaint = new Paint();
        mPaint.setStrokeWidth(15f);
        mPaint.setColor(Color.BLACK);

        setModel(model);
        mFirstDisplay = true;

        mPupilRadius = context.getResources().getDimensionPixelSize(R.dimen.eye_pupil_diameter);
    }

    /**
     * set GooglyPet model for the view
     *
     * @param model GooglyPet from GoolyPetFactory
     */
    public void setModel(GooglyPet model) {
        //update model
        mGooglyPetModel = model;

        //update pet skin
        this.setImageResource(mGooglyPetModel.getPetRes());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mGooglyPetModel != null) {
            float middleW = canvas.getWidth();
            float middleH = canvas.getHeight();

            final GooglyEye leftEye = mGooglyPetModel.getLeftEye();
            final GooglyEye rightEye = mGooglyPetModel.getRightEye();

            if (leftEye != null && leftEye.isOpened()) {
                //draw left eye
                canvas.drawCircle(leftEye.getCenterX() * middleW + leftEye.getOrientationX(),
                        leftEye.getCenterY() * middleH + leftEye.getOrientationY(),
                        mPupilRadius, mPaint);
            }

            if (rightEye != null && rightEye.isOpened()) {
                //draw right eye
                canvas.drawCircle(rightEye.getCenterX() * middleW + rightEye.getOrientationX(),
                        rightEye.getCenterY() * middleH + rightEye.getOrientationY(),
                        mPupilRadius, mPaint);
            }
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mFirstDisplay) {
            moveDown();
            mFirstDisplay = false;
        } else if (!mGooglyPetModel.isAwake()) {
            moveDown();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    /**
     * animate the view to move down from 70% of its height
     */
    public void moveDown() {
        ObjectAnimator trans = ObjectAnimator.ofFloat(GooglyPetView.this, "translationY", GooglyPetView.this.getHeight() * mGooglyPetModel.getBodyProportion());
        trans.setInterpolator(new DecelerateInterpolator());
        trans.setDuration(300);
        trans.start();
    }


    /**
     * animate the view to move up to 25% of its height
     */
    public void moveUp() {
        ObjectAnimator trans = ObjectAnimator.ofFloat(GooglyPetView.this, "translationY", GooglyPetView.this.getHeight() * mGooglyPetModel.getHeadProportion());
        trans.setInterpolator(new DecelerateInterpolator());
        trans.setDuration(300);
        trans.start();
    }


    /**
     * Animate eyes looking at the given direction
     *
     * @param newDirection new direction for both eyes
     */
    public void animatePetEyes(float[] newDirection) {
        if (mGooglyPetModel != null) {
            final GooglyEye leftEye = mGooglyPetModel.getLeftEye();
            final GooglyEye rightEye = mGooglyPetModel.getRightEye();

            if (leftEye.isOpened()) {
                leftEye.blink();
            } else {
                leftEye.open();
            }

            if (rightEye.isOpened()) {
                rightEye.blink();
            } else {
                rightEye.open();
            }

            leftEye.setOrientationX(newDirection[0]);
            leftEye.setOrientationY(newDirection[1]);
            rightEye.setOrientationX(newDirection[0]);
            rightEye.setOrientationY(newDirection[1]);

            this.invalidate();
        }
    }
}