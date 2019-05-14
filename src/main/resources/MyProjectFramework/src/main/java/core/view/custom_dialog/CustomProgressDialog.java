package $Package.core.view.custom_dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import $Package.R;

import java.text.NumberFormat;

/**
 * 自定义ProgressDialog
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class CustomProgressDialog implements OnClickListener {
	   
    /** Creates a ProgressDialog with a ciruclar, spinning progress
     * bar. This is the default.
     */
    public static final int STYLE_SPINNER = 0;
    
    /** Creates a ProgressDialog with a horizontal progress bar.
     */
    public static final int STYLE_HORIZONTAL = 1;
    
    private CustomAlertDialog mCustomAlertDialog;
    private NumberProgressBar mProgress;
    private TextView mMessageView;
    
    private int mProgressStyle = STYLE_SPINNER;
    private String mProgressNumberFormat;
    private TextView mProgressPercent;
    private NumberFormat mProgressPercentFormat;
    
    private int mMax;
    private int mProgressVal;
    private int mSecondaryProgressVal;
    private int mIncrementBy;
    private int mIncrementSecondaryBy;
    private Drawable mProgressDrawable;
    private Drawable mIndeterminateDrawable;
    private CharSequence mMessage;
    private boolean mIndeterminate;
    
    private boolean mHasStarted;
    private Handler mViewUpdateHandler;
    
    private Context mContext;
    private String mTitle;
    private boolean mCancelable;
    private OnCancelListener mCancelListener;

	private CustomAlertDialog.Builder builder;

	private Button btn;
	private DialogInterface.OnClickListener mNegativeButtonOnClickListener;
    private int color;

    public CustomProgressDialog(Context context){
    	this(context, null);
    }
    
    public CustomProgressDialog(Context context, String title){
    	this(context, STYLE_HORIZONTAL, title, false, null);
    }

    public CustomProgressDialog(Context context, int progressStyle,String title, boolean cancelable,
			OnCancelListener cancelListener) {
		//super(context, cancelable, cancelListener);
    	mContext = context;
    	mProgressStyle = progressStyle;
    	mTitle = title;
    	mCancelable = cancelable;
    	mCancelListener = cancelListener;
	}
    
    @SuppressLint("HandlerLeak")
	private CustomAlertDialog get(Context context, String title,
            CharSequence message, boolean indeterminate,
            boolean cancelable, OnCancelListener cancelListener){
    	LayoutInflater inflater = LayoutInflater.from(context);
    	builder =  new CustomAlertDialog.Builder(context);
    	
            /* Use a separate handler to update the text views as they
             * must be updated on the same thread that created them.
             */
            mViewUpdateHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    
                    /* Update the number and percent */
                    int progress = mProgress.getProgress();
                    int max = mProgress.getMax();
                    double percent = (double) progress / (double) max;
                    SpannableString tmp = new SpannableString(mProgressPercentFormat.format(percent));
                    tmp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                            0, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mProgressPercent.setText(tmp);
                }
            };
        
        View view;
        if (mProgressStyle == STYLE_HORIZONTAL) {
            view = inflater.inflate(R.layout.custom_dialog_progress_hori, null);
            btn = (Button) view.findViewById(R.id.btn);
            btn.setOnClickListener(this);
        } else {
            view = inflater.inflate(R.layout.custom_dialog_progress_cycle, null);
        }
        
//        mProgress = (ProgressBar) view.findViewById(android.R.id.progress);
        mProgress = (NumberProgressBar) view.findViewById(android.R.id.progress);
        if (color != 0) {
            mProgress.setProgressTextColor(color);
            mProgress.setReachedBarColor(color);
        }

        mProgressNumberFormat = "%d/%d";
        mProgressPercent = (TextView) view.findViewById(android.R.id.text1);
        mMessageView = (TextView) view.findViewById(android.R.id.message);
        mProgressPercentFormat = NumberFormat.getPercentInstance();
        mProgressPercentFormat.setMaximumFractionDigits(0);
        builder.setTheme(color);
        builder.setView(view);
        builder.setTitle(title);
        builder.setCancelable(cancelable);
        builder.setOnCancelListener(cancelListener);
        if (mMax > 0) {
            setMax(mMax);
        }
        if (mProgressVal > 0) {
            setProgress(mProgressVal);
        }
//        if (mSecondaryProgressVal > 0) {
//            setSecondaryProgress(mSecondaryProgressVal);
//        }
//        if (mIncrementBy > 0) {
//            incrementProgressBy(mIncrementBy);
//        }
//        if (mIncrementSecondaryBy > 0) {
//            incrementSecondaryProgressBy(mIncrementSecondaryBy);
//        }
//        if (mProgressDrawable != null) {
//            setProgressDrawable(mProgressDrawable);
//        }
//        if (mIndeterminateDrawable != null) {
//            setIndeterminateDrawable(mIndeterminateDrawable);
//        }
        if (mMessage != null) {
            setMessage(mMessage);
        }
//        setIndeterminate(mIndeterminate);
        onProgressChanged();
        
        mCustomAlertDialog = builder.create(CustomAlertDialog.PROGRESS);
    	return mCustomAlertDialog;
    }
    
    public void create(){
    	if (mCustomAlertDialog == null){
    		get(mContext, mTitle, mMessage, false, mCancelable, mCancelListener);
    	}
    }
    

    public void show(){
    	create();
    	mCustomAlertDialog.show();
        mHasStarted = true;
    }
    
    public void dismiss(){
    	if (mCustomAlertDialog != null){
	    	mCustomAlertDialog.dismiss();
    	}
        mHasStarted = false;
    }
    
    public void setProgress(int value) {
        if (mHasStarted) {
            mProgress.setProgress(value);
            onProgressChanged();
        } else {
            mProgressVal = value;
        }
    }

//    public void setSecondaryProgress(int secondaryProgress) {
//        if (mProgress != null) {
//            mProgress.setSecondaryProgress(secondaryProgress);
//            onProgressChanged();
//        } else {
//            mSecondaryProgressVal = secondaryProgress;
//        }
//    }

    public int getProgress() {
        if (mProgress != null) {
            return mProgress.getProgress();
        }
        return mProgressVal;
    }

//    public int getSecondaryProgress() {
//        if (mProgress != null) {
//            return mProgress.getSecondaryProgress();
//        }
//        return mSecondaryProgressVal;
//    }

    public int getMax() {
        if (mProgress != null) {
            return mProgress.getMax();
        }
        return mMax;
    }

    public void setMax(int max) {
        if (mProgress != null) {
            mProgress.setMax(max);
            onProgressChanged();
        } else {
            mMax = max;
        }
    }

    public void incrementProgressBy(int diff) {
        if (mProgress != null) {
            mProgress.incrementProgressBy(diff);
            onProgressChanged();
        } else {
            mIncrementBy += diff;
        }
    }

//    public void incrementSecondaryProgressBy(int diff) {
//        if (mProgress != null) {
//            mProgress.incrementSecondaryProgressBy(diff);
//            onProgressChanged();
//        } else {
//            mIncrementSecondaryBy += diff;
//        }
//    }

//    public void setProgressDrawable(Drawable d) {
//        if (mProgress != null) {
//            mProgress.setProgressDrawable(d);
//        } else {
//            mProgressDrawable = d;
//        }
//    }

//    public void setIndeterminateDrawable(Drawable d) {
//        if (mProgress != null) {
//            mProgress.setIndeterminateDrawable(d);
//        } else {
//            mIndeterminateDrawable = d;
//        }
//    }

//    public void setIndeterminate(boolean indeterminate) {
//        if (mProgress != null) {
//            mProgress.setIndeterminate(indeterminate);
//        } else {
//            mIndeterminate = indeterminate;
//        }
//    }

//    public boolean isIndeterminate() {
//        if (mProgress != null) {
//            return mProgress.isIndeterminate();
//        }
//        return mIndeterminate;
//    }
    
    public void setMessage(CharSequence message) {
        if (mProgress != null) {
            if (mProgressStyle == STYLE_HORIZONTAL) {
               // super.setMessage(message);
            	mMessageView.setText(message);
            } else {
                mMessageView.setText(message);
            }

        } else {
            mMessage = message;
        }
    }
    
    public void setProgressStyle(int style) {
        mProgressStyle = style;
    }

    /**
     * Change the format of Progress Number. The default is "current/max".
     * Should not be called during the number is progressing.
     * @param format Should contain two "%d". The first is used for current number
     * and the second is used for the maximum.
     * @hide
     */
    public void setProgressNumberFormat(String format) {
        mProgressNumberFormat = format;
    }
    
    private void onProgressChanged() {
        if (mProgressStyle == STYLE_HORIZONTAL) {
            mViewUpdateHandler.sendEmptyMessage(0);
        }
    }

	/**
	 * @return the builder
	 */
	public CustomAlertDialog.Builder getBuilder() {
		return builder;
	}

	/**
	 * @param builder the builder to set
	 */
	public void setBuilder(CustomAlertDialog.Builder builder) {
		this.builder = builder;
	}

	@Override
	public void onClick(View v) {
		mNegativeButtonOnClickListener.onClick(mCustomAlertDialog, 0);
	}    
	public void setNegativeButton(String text, DialogInterface.OnClickListener listener){
		btn.setText(text);
		mNegativeButtonOnClickListener = listener;
	}

    /**
     * 设置颜色主题
     * @param color
     */
    public void setTheme(int color) {
        this.color = color;
    }
}
