package me.qiao.giflib.movie.lib1;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

@TargetApi(16)
public class MovieImageView extends ImageView {

	private static final boolean DB = false;
	private static final boolean DB_DETAIL = false;
	private static final String DB_TAG = "MovieImageView";

	/** Feature Support GIF */
	private static final boolean FEATURE_IS_GIF_SUPPORTED = true;

	/** @see #syncParentParameter() */
	private int mSuperPaddingTop;
	private int mSuperPaddingLeft;
	private int mSuperPaddingRight;
	private int mSuperPaddingBottom;
	private ScaleType mSuperScaleType;
	private Matrix mSuperDrawMatrix;

	/** mMovie==null means we work the same as parent(ImageView) */
	private Movie mMovie = null;

	private Matrix mMatrix;
	private Matrix mDrawMatrix;

	private long mMovieStartTime = 0;
	private long mMovieDuration = 0;

	private int mDefLayerType;

	// AdjustViewBounds behavior will be in compatibility mode for older apps.
	private boolean mAdjustViewBoundsCompat = false;

	private boolean mHasFrame = false;

	public MovieImageView(Context context) {
		super(context);
		initGifAndImageView();
	}

	public MovieImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MovieImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initGifAndImageView();
	}

	private void initGifAndImageView() {
		Log("initGifAndImageView");
		if (FEATURE_IS_GIF_SUPPORTED) {
			mMatrix = new Matrix();
			mDefLayerType = getLayerType();
		}
		mAdjustViewBoundsCompat = this.getContext().getApplicationInfo().targetSdkVersion <= Build.VERSION_CODES.JELLY_BEAN_MR1;
	}

	public void setGifFile(File file){
		setMovie(Movie.decodeFile(file.getAbsolutePath()));
	}

	/**
	 * You may open an inputstream of certain GIF file, and then decode by
	 * Movie.decodeStream.
	 *
	 * @param movie
	 */
	public void setMovie(Movie movie) {
		Log("setMovie");
		if (FEATURE_IS_GIF_SUPPORTED && mMovie != movie) {
			mMovie = movie;
			if (mMovie != null) {
				prepareForMovie(true);
				mMovieDuration = mMovie.duration();
				requestLayout();
			} else {
				prepareForMovie(false);
			}
			invalidate();
		}
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		prepareForMovie(false);
		super.setImageBitmap(bm);
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		prepareForMovie(false);
		super.setImageDrawable(drawable);
	}

	@Override
	public void setImageResource(int resId) {
		prepareForMovie(false);
		super.setImageResource(resId);
	}

	@Override
	public void setImageURI(Uri uri) {
		prepareForMovie(false);
		super.setImageURI(uri);
	}

	private void prepareForMovie(boolean isToDo) {
		if (FEATURE_IS_GIF_SUPPORTED && isToDo) {
			if (getLayerType() != View.LAYER_TYPE_SOFTWARE) {
				setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			}
			setWillNotCacheDrawing(false);
			mMovieStartTime = 0;
		} else {
			if (mDefLayerType != 0 && mDefLayerType != getLayerType()) {
				setLayerType(mDefLayerType, null);
			}
			mMovie = null;
		}
	}

	@Override
	public void setPadding(int left, int top, int right, int bottom) {
		super.setPadding(left, top, right, bottom);
		this.requestLayout();
	}

	@Override
	public void setImageMatrix(Matrix matrix) {
		super.setImageMatrix(matrix);
		// We should do the following whether we are in "MovieMode" or not,
		// because we can not get the matrix from
		// parent later.
		if (matrix == null && !mMatrix.isIdentity() || matrix != null && !mMatrix.equals(matrix)) {
			mMatrix.set(matrix);
			configureDrawMatrix();
			invalidate();
		}
	}

	@Override
	protected boolean setFrame(int l, int t, int r, int b) {
		Log("setFrame");
		boolean changed = super.setFrame(l, t, r, b);
		mHasFrame = true;
		configureDrawMatrix();
		return changed;
	}

	private void configureDrawMatrix() {
		Log("configureDrawMatrix");
		if (!FEATURE_IS_GIF_SUPPORTED || mMovie == null || !mHasFrame) {
			return;
		}

		syncParentParameter();

		int movieWidth = mMovie.width();// in pixel
		int movieHeight = mMovie.height();
		Log("movieWidth = " + movieWidth + ", movieHeight = " + movieHeight);

		// in pixels
		int vWidth = getWidth() - mSuperPaddingLeft - mSuperPaddingRight;
		int vHeight = getHeight() - mSuperPaddingTop - mSuperPaddingBottom;

		Log("vWidth = " + vWidth + ", vHeight = " + vHeight);

		if (ScaleType.CENTER == mSuperScaleType) {
			mDrawMatrix = mMatrix;
			mDrawMatrix.setTranslate((int) ((vWidth - movieWidth) * 0.5f + 0.5f),
					(int) ((vHeight - movieHeight) * 0.5f + 0.5f));

		} else if (ScaleType.CENTER_CROP == mSuperScaleType) {

			mDrawMatrix = mMatrix;
			float scale = Math.max((float) vHeight / (float) movieHeight, (float) vWidth / (float) movieWidth);
			float dx = (vWidth - movieWidth * scale) * 0.5f;
			float dy = (vHeight - movieHeight * scale) * 0.5f;
			mDrawMatrix.setScale(scale, scale);
			mDrawMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));

			Log("CENTER_CROP : scale=" + scale + ", dx=" + dx + ", dy=" + dy);

		} else if (ScaleType.CENTER_INSIDE == mSuperScaleType) {

			mDrawMatrix = mMatrix;
			float scale;
			if (movieWidth <= vWidth && movieHeight <= vHeight) {
				scale = 1.0f;
			} else {
				scale = Math.min((float) vWidth / (float) movieWidth, (float) vHeight / (float) movieHeight);
			}
			float dx = (int) ((vWidth - movieWidth * scale) * 0.5f + 0.5f);
			float dy = (int) ((vHeight - movieHeight * scale) * 0.5f + 0.5f);
			mDrawMatrix.setScale(scale, scale);
			mDrawMatrix.postTranslate(dx, dy);

			Log("CENTER_INSIDE : scale=" + scale + ", dx=" + dx + ", dy=" + dy);

		} else if (ScaleType.FIT_XY == mSuperScaleType) {

			mDrawMatrix = mMatrix;
			float scaleX = (float) vWidth / (float) movieWidth;
			float scaleY = (float) vHeight / (float) movieHeight;
			Log("ScaleType.FIT_XY, scaleX = " + scaleX + ", scaleY = " + scaleY);
			mDrawMatrix.setScale(scaleX, scaleY);
			// mDrawMatrix.postTranslate(mSuperPaddingLeft, mSuperPaddingTop);

		} else if (ScaleType.MATRIX == mSuperScaleType) {

			mDrawMatrix = mSuperDrawMatrix;

		} else { /* fit */
			mDrawMatrix = mMatrix;
			float scale = Math.min((float) vHeight / (float) movieHeight, (float) vWidth / (float) movieWidth);
			float dx = 0.0f;
			float dy = 0.0f;
			if (ScaleType.FIT_START == mSuperScaleType) {
				// dx = 0.0f;
				// dy = 0.0f;
			} else if (ScaleType.FIT_CENTER == mSuperScaleType) {
				dx = (vWidth - movieWidth * scale) * 0.5f + 0.5f;
				dy = (vHeight - movieHeight * scale) * 0.5f + 0.5f;
				// dx = (vWidth - movieWidth * scale) * 0.5f;
				// dy = (vHeight - movieHeight * scale) * 0.5f;
			} else {/* ScaleType.FIT_END == mSuperScaleType */
				dx = vWidth - movieWidth * scale;
				dy = vHeight - movieHeight * scale;
			}
			mDrawMatrix.setScale(scale, scale);
			mDrawMatrix.postTranslate((int) dx, (int) dy);

			Log("fit : scale=" + scale + ", dx=" + dx + ", dy=" + dy);
		}
	}

	private void syncParentParameter() {
		Log("syncParentParameter");
		mSuperPaddingTop = getPaddingTop();
		mSuperPaddingLeft = getPaddingLeft();
		mSuperPaddingRight = getPaddingRight();
		mSuperPaddingBottom = getPaddingBottom();
		mSuperScaleType = getScaleType();
		mSuperDrawMatrix = getImageMatrix();

		Log("====syncParentParameter====");
		Log("Padding: Top/Left/Right/Botton=[" + mSuperPaddingTop + "/" + mSuperPaddingLeft + "/" + mSuperPaddingRight
				+ "/" + mSuperPaddingBottom + "]");
		Log("ScaleType: " + mSuperScaleType);
		Log("DrawMatrix: " + mSuperDrawMatrix);
		Log("===========================");
	}

	@Override
	public void setWillNotCacheDrawing(boolean willNotCacheDrawing) {
		Log("setWillNotCacheDrawing,  willNotCacheDrawing=" + willNotCacheDrawing);
		if (FEATURE_IS_GIF_SUPPORTED && mMovie != null) {
			super.setWillNotCacheDrawing(false);
		} else {
			super.setWillNotCacheDrawing(willNotCacheDrawing);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		Log("onMeasure");

		if (!FEATURE_IS_GIF_SUPPORTED || mMovie == null) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}

		syncParentParameter();

		int w;
		int h;

		// Desired aspect ratio of the view's contents (not including padding)
		float desiredAspect = 0.0f;

		// We are allowed to change the view's width
		boolean resizeWidth = false;

		// We are allowed to change the view's height
		boolean resizeHeight = false;

		final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

		if (mMovie == null) {
			w = h = 0;
		} else {
			w = mMovie.width();
			h = mMovie.height();
			Log("onMeasure, w = " + w + ", h = " + h);
			if (w <= 0)
				w = 1;
			if (h <= 0)
				h = 1;

			// We are supposed to adjust view bounds to match the aspect
			// ratio of our drawable. See if that is possible.
			if (getAdjustViewBounds()) {
				resizeWidth = widthSpecMode != MeasureSpec.EXACTLY;
				resizeHeight = heightSpecMode != MeasureSpec.EXACTLY;
				desiredAspect = (float) w / (float) h;
			}
		}

		int widthSize, heightSize;

		Log("onMeasure, resizeWidth=" + resizeWidth + ", resizeHeight=" + resizeHeight);
		if (resizeWidth || resizeHeight) {

			int maxWidth = getMaxWidth();
			int maxHeight = getMaxHeight();
			/*
			 * If we get here, it means we want to resize to match the drawables
			 * aspect ratio, and we have the freedom to change at least one
			 * dimension.
			 */

			// Get the max possible width given our constraints
			widthSize = resolveAdjustedSize(w + mSuperPaddingLeft + mSuperPaddingRight, maxWidth, widthMeasureSpec);

			// Get the max possible height given our constraints
			heightSize = resolveAdjustedSize(h + mSuperPaddingTop + mSuperPaddingBottom, maxHeight, heightMeasureSpec);

			if (desiredAspect != 0.0f) {
				// See what our actual aspect ratio is
				float actualAspect = (float) (widthSize - mSuperPaddingLeft - mSuperPaddingRight)
						/ (heightSize - mSuperPaddingTop - mSuperPaddingBottom);

				if (Math.abs(actualAspect - desiredAspect) > 0.0000001) {

					boolean done = false;

					// Try adjusting width to be proportional to height
					if (resizeWidth) {
						int newWidth = (int) (desiredAspect * (heightSize - mSuperPaddingTop - mSuperPaddingBottom))
								+ mSuperPaddingLeft + mSuperPaddingRight;

						// Allow the width to outgrow its original estimate if
						// height is fixed.
						if (!resizeHeight && !mAdjustViewBoundsCompat) {
							widthSize = resolveAdjustedSize(newWidth, maxWidth, widthMeasureSpec);
						}

						if (newWidth <= widthSize) {
							widthSize = newWidth;
							done = true;
						}
					}

					// Try adjusting height to be proportional to width
					if (!done && resizeHeight) {
						int newHeight = (int) ((widthSize - mSuperPaddingLeft - mSuperPaddingRight) / desiredAspect)
								+ mSuperPaddingTop + mSuperPaddingBottom;

						// Allow the height to outgrow its original estimate if
						// width is fixed.
						if (!resizeWidth && !mAdjustViewBoundsCompat) {
							heightSize = resolveAdjustedSize(newHeight, maxHeight, heightMeasureSpec);
						}

						if (newHeight <= heightSize) {
							heightSize = newHeight;
						}
					}
				}
			}
		} else {
			/*
			 * We are either don't want to preserve the drawables aspect ratio,
			 * or we are not allowed to change view dimensions. Just measure in
			 * the normal way.
			 */
			w += mSuperPaddingLeft + mSuperPaddingRight;
			h += mSuperPaddingTop + mSuperPaddingBottom;

			w = Math.max(w, getSuggestedMinimumWidth());
			h = Math.max(h, getSuggestedMinimumHeight());

			widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
			heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);
		}

		Log("onMeasure, widthSize=" + widthSize + ", heightSize=" + heightSize);
		setMeasuredDimension(widthSize, heightSize);
	}

	private int resolveAdjustedSize(int desiredSize, int maxSize, int measureSpec) {
		Log("resolveAdjustedSize, desiredSize=" + desiredSize + ",maxSize=" + maxSize + ",measureSpec=" + measureSpec);
		int result = desiredSize;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		switch (specMode) {
			case MeasureSpec.UNSPECIFIED:
			/*
			 * Parent says we can be as big as we want. Just don't be larger
			 * than max size imposed on ourselves.
			 */
				result = Math.min(desiredSize, maxSize);
				break;
			case MeasureSpec.AT_MOST:
				// Parent says we can be as big as we want, up to specSize.
				// Don't be larger than specSize, and don't be larger than
				// the max size imposed on ourselves.
				result = Math.min(Math.min(desiredSize, specSize), maxSize);
				break;
			case MeasureSpec.EXACTLY:
				// "60dp"
				// No choice. Do what we are told.
				result = specSize;
				break;
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (!FEATURE_IS_GIF_SUPPORTED || mMovie == null) {
			super.onDraw(canvas);
			return;
		}

		// Movie set time
		if (mMovieDuration == 0) {
			mMovie.setTime(0);
		} else {
			long now = android.os.SystemClock.uptimeMillis();
			if (mMovieStartTime == 0) {
				mMovieStartTime = now;// first time
			}
			mMovie.setTime((int) ((now - mMovieStartTime) % mMovieDuration));
		}

		// save the current matrix and clip of canvas
		int saveCount = canvas.getSaveCount();
		canvas.save();

		boolean superCropToPadding = getCropToPadding();
		Log("superCropToPadding = " + superCropToPadding);
		if (superCropToPadding) {
			int superScrollX = getScrollX();
			int superScrollY = getScrollY();
			int superRight = getRight();
			int superLeft = getLeft();
			int superBottom = getBottom();
			int superTop = getTop();
			canvas.clipRect(superScrollX + mSuperPaddingLeft, superScrollY + mSuperPaddingTop, superScrollX
					+ superRight - superLeft - mSuperPaddingRight, superScrollY + superBottom - superTop
					- mSuperPaddingBottom);
		}

		canvas.translate(mSuperPaddingLeft, mSuperPaddingTop);// yeqi.zhang@20131031

		if (mDrawMatrix != null && !mDrawMatrix.isIdentity()) {
			canvas.concat(mDrawMatrix);
		}
		// Log("onDraw : mSuperPaddingLeft=" + mSuperPaddingLeft +
		// ", mSuperPaddingTop=" + mSuperPaddingTop);
		mMovie.draw(canvas, 0, 0);

		canvas.restoreToCount(saveCount);
		invalidate();
	}

	private static void Log(String log) {
		if (DB) {
			Log.i(DB_TAG, log);
		}
	}
}
