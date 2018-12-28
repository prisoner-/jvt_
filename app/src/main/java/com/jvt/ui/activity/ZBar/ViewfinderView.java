package com.jvt.ui.activity.ZBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.jvt.R;
import com.jvt.utils.DensityUtil;

import java.util.Collection;
import java.util.HashSet;

@SuppressLint("DrawAllocation")
public final class ViewfinderView extends View {

	private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192,
			128, 64 };
	private static final long ANIMATION_DELAY = 100L;
	private static final int OPAQUE = 0xFF;

	private Paint paint;
	private Bitmap resultBitmap;
	private int maskColor;
	private int resultColor;
	private int frameColor;
	private int laserColor;
	private int resultPointColor;
	private int scannerAlpha;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;
	private boolean laserLinePortrait = true;
	int i = 0;
	private Rect mRect;
	private GradientDrawable mDrawable;
	private Rect frame;

	public ViewfinderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initedata();
	}

	public ViewfinderView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initedata();
	}

	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Initialize these once for performance rather than calling them every
		// time in onDraw().
		initedata();
	}

	void initedata() {
		mRect = new Rect();
		int left = Color.parseColor("#60CACACA");
		int center = Color.parseColor("#6000ff00");
		int right = Color.parseColor("#60CACACA");
		mDrawable = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM, new int[] {right, center
						 });
		int r = 8;
		mDrawable.setShape(GradientDrawable.RECTANGLE);
		mDrawable
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		//setCornerRadii(mDrawable, r, r, r, r);
		paint = new Paint();
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);
		frameColor = resources.getColor(R.color.viewfinder_frame);
		laserColor = resources.getColor(R.color.viewfinder_laser);
		resultPointColor = resources.getColor(R.color.possible_result_points);
		scannerAlpha = 0;
		possibleResultPoints = new HashSet<ResultPoint>(5);
		setFrame();
	}

	public Rect getFrame() {
		return frame;
	}
	int w ;
	public void setFrame() {
		int screenw = DensityUtil.getScreenWidth(getContext());
		int screenh = DensityUtil.getScreenHeight(getContext());
		w= DensityUtil.dip2px(getContext(), 250);
		Rect frame = new Rect((screenw - w) / 2, (screenh - w) / 2,
				(screenw - w) / 2 + w, (screenh - w) / 2 + w);
		this.frame = frame;
	}

	@Override
	public void onDraw(Canvas canvas) {
		// frame = new Rect(getLeft()+100, getTop()+100, getRight()-100,
		// getBottom()-100);
		if (frame == null) {
			return;
		}
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// Draw the exterior (i.e. outside the framing rect) darkened
		paint.setColor(resultBitmap != null ? resultColor : maskColor);
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
				paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);

		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		} else {

			// Draw a two pixel solid black border inside the framing rect
			canvas.drawRect(frame.left, frame.top, frame.right + 1,
					frame.top + 2, paint);
			canvas.drawRect(frame.left, frame.top + 2, frame.left + 2,
					frame.bottom - 1, paint);
			canvas.drawRect(frame.right - 1, frame.top, frame.right + 1,
					frame.bottom - 1, paint);
			canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1,
					frame.bottom + 1, paint);

			// show decoding is active
			paint.setColor(laserColor);
			paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
			scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
			int middle = frame.height() / 2 + frame.top;

			if (laserLinePortrait) {
				if (laserLinePortrait) {

					if ((i += 10) < frame.bottom - frame.top) {
						/*
						 * canvas.drawRect(frame.left + 2, frame.top - 2 + i,
						 * frame.right - 1, frame.top + 2 + i, paint);
						 */


						mRect.set(frame.left + 2, frame.top-2 ,
								frame.right - 1, frame.top  + i);
						mDrawable.setBounds(mRect);
						mDrawable.draw(canvas);
						invalidate();
					} else {
						i = 0;
						
					}

				} else {
					float left = frame.left + (frame.right - frame.left) / 2
							- 2;
					canvas.drawRect(left, frame.top, left + 2,
							frame.bottom - 2, paint);
				}
				// canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1,
				// middle + 2, paint);
			} else {
				float left = frame.left + (frame.right - frame.left) / 2 - 2;
				float top = frame.top - (frame.right - frame.left) / 2 - 2;
				canvas.drawRect(left, frame.top, left + 2, frame.bottom - 2,
						paint);
			}
			Collection<ResultPoint> currentPossible = possibleResultPoints;
			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			} else {
				possibleResultPoints = new HashSet<ResultPoint>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(OPAQUE);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentPossible) {
					// canvas.drawCircle(frame.left + point.getX(), frame.top +
					// point.getY(), 6.0f, paint);
					canvas.drawCircle(frame.left + point.getY(), frame.top
							+ point.getX(), 6.0f, paint);
				}
			}
			if (currentLast != null) {
				paint.setAlpha(OPAQUE / 2);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentLast) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 3.0f, paint);
				}
			}

			// Request another update at the animation interval, but only
			// repaint the laser line,
			// not the entire viewfinder mask.
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
					frame.right, frame.bottom);
		}
	}

	public void setCornerRadii(GradientDrawable drawable, float r0, float r1,
                               float r2, float r3) {
		drawable.setCornerRadii(new float[] { r0, r0, r1, r1, r2, r2, r3, r3 });
	}

	public void changeLaser() {
		if (laserLinePortrait) {
			laserLinePortrait = false;
		} else {
			laserLinePortrait = true;
		}
	}

	public void drawViewfinder() {
		resultBitmap = null;
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

}
