package $Package.core.view.custom_dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import $Package.R;
import $Package.core.fuction.DensityUtil;

import java.lang.ref.WeakReference;

/**
 * 模仿alertDialog编写。<br>
 * 可以直接使用其builder来创建自定义对话框
 * Created by Vincent on $Time.
 */
public class CustomAlertDialog extends Dialog implements DialogInterface.OnKeyListener {


	/**
	 * PROGRESS类型提示框
	 */
	public static final int PROGRESS = 0;
	/**
	 * 普通类型
	 */
	public static final int NORMAL = 1;
	private Builder builder;
	private int type;

	public CustomAlertDialog(Context context, boolean cancelable,
                             OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		setOnKeyListener(this);
	}

	public CustomAlertDialog(Context context, int theme) {
		super(context, theme);
		setOnKeyListener(this);
	}

	public CustomAlertDialog(Context context) {
		super(context);
		setOnKeyListener(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//LayoutParams params = getWindow().getAttributes();
		//params.horizontalMargin = 20;
		//getWindow().setAttributes(params);
	}


	public static class Builder implements OnDismissListener {
		private OnKeyDown onKeyDown;
		private Context mContext;
		private CustomAlertDialog mDialog;
		private String mTitle;
		private String mMessage;
		private Button mBtnPositive, mBtnNegative, mBtnNeutral;
		private Message mButtonPositiveMessage, mButtonNegativeMessage, mButtonNeutralMessage;
		private String mPositiveButtonText;
		private String mNegativeButtonText;
		private String mNeutralButtonText;
		private OnClickListener
			mPositiveButtonOnClickListener, mNegativeButtonOnClickListener, mNeutralButtonOnClickListener;
		private Handler mHandler;
		private OnCancelListener mOnCancelListener;
		
		private ListView mListView;
		private ListAdapter mListAdapter;
		private String[] mItemArray;
		private int mSelectedItemtIndex = 0;
		private OnClickListener mItemClickListener;
		private boolean mIsSingleChoice = false;
		private View mDialogTemplate;
		
		private View mViewContent;
		private boolean mCancelable = true;
		
		private int mLayout = 0;
        private OnDismiss onDismiss;
        private int type;
		private int color;
		private int messageTextSize = 15;


		public Builder (Context context){
			mContext = context;
		}
		
		View.OnClickListener mButtonHandler = new View.OnClickListener() {
	        public void onClick(View v) {
	            Message m = null;
	            if (v == mBtnPositive && mButtonPositiveMessage != null) {
	                m = Message.obtain(mButtonPositiveMessage);
	            } else if (v == mBtnNegative && mButtonNegativeMessage != null) {
	                m = Message.obtain(mButtonNegativeMessage);
	            } else if (v == mBtnNeutral && mButtonNeutralMessage != null) {
	                m = Message.obtain(mButtonNeutralMessage);
	            }
	            if (m != null) {
	                m.sendToTarget();
	            }

	            // Post a message so we dismiss after the above handlers are executed
	            mHandler.obtainMessage(ButtonHandler.MSG_DISMISS_DIALOG, mDialog)
	                    .sendToTarget();
	        }
	    };

		/**
		 * 设置颜色主题
		 * @param color
		 */
		public void setTheme(int color) {
			this.color = color;
		}

		/**
		 * 设置消息文本大小
		 * @param messageTextSize
		 */
		public void setMessageTextSize(int messageTextSize) {
			this.messageTextSize = messageTextSize;
		}

		public void dismiss() {
			mDialog.dismiss();

		}

		public boolean isShowing() {
			return mDialog.isShowing();
		}

		private static final class ButtonHandler extends Handler {
	        // Button clicks have Message.what as the BUTTON{1,2,3} constant
	        private static final int MSG_DISMISS_DIALOG = 1;
	        
	        private WeakReference<DialogInterface> mDialog;

	        public ButtonHandler(DialogInterface dialog) {
	            mDialog = new WeakReference<DialogInterface>(dialog);
	        }

	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	                
	                case DialogInterface.BUTTON_POSITIVE:
	                case DialogInterface.BUTTON_NEGATIVE:
	                case DialogInterface.BUTTON_NEUTRAL:
	                    ((OnClickListener) msg.obj).onClick(mDialog.get(), msg.what);
	                    break;
	                    
	                case MSG_DISMISS_DIALOG:
	                    ((DialogInterface) msg.obj).dismiss();
	            }
	        }
	    }
		
		public Builder setTitle(String title){
			mTitle = title;
			return this;
		}
		
		public Builder setTitle(int resId){
			mTitle = mContext.getString(resId);
			return this;
		}
		
		public Builder setMessage(String msg){
			mMessage = msg;
			return this;
		}
		
		public Builder setMessage(int resId){
			mMessage = mContext.getString(resId);
			return this;
		}
		
		public Builder setPositiveButton(String text, OnClickListener listener){
			mPositiveButtonText = text;
			mPositiveButtonOnClickListener = listener;
			//setButton(DialogInterface.BUTTON_POSITIVE, text, listener, mButtonPositiveMessage);
			return this;
		}
		
		public Builder setPositiveButton(int resId, OnClickListener listener){
			mPositiveButtonText = mContext.getString(resId);
			mPositiveButtonOnClickListener = listener;
			//setButton(DialogInterface.BUTTON_POSITIVE, mPositiveButtonText, listener, mButtonPositiveMessage);
			return this;
		}
		
		public Builder setNegativeButton(String text, OnClickListener listener){
			mNegativeButtonText = text;
			mNegativeButtonOnClickListener = listener;
			//setButton(DialogInterface.BUTTON_NEGATIVE, mNegativeButtonText, listener, mButtonNegativeMessage);
			return this;
		}
		
		public Builder setNegativeButton(int resId, OnClickListener listener){
			mNegativeButtonText = mContext.getString(resId);
			mNegativeButtonOnClickListener = listener;
			//setButton(DialogInterface.BUTTON_NEGATIVE, mNegativeButtonText, listener, mButtonNegativeMessage);
			return this;
		}
		
		public Builder setNeutralButton(String text, OnClickListener listener){
			mNeutralButtonText = text;
			mNeutralButtonOnClickListener = listener;
			//setButton(DialogInterface.BUTTON_NEGATIVE, mNeutralButtonText, listener, mButtonNeutralMessage);
			return this;
		}
		
		public Builder setNeutralButton(int resId, OnClickListener listener){
			mNeutralButtonText = mContext.getString(resId);
			mNeutralButtonOnClickListener = listener;
			
			return this;
		}
		
	    public void setButton(int whichButton, 
	            OnClickListener listener, Message msg) {

	        if (msg == null && listener != null) {
	            msg = mHandler.obtainMessage(whichButton, listener);
	        }
	        
	        switch (whichButton) {

	            case DialogInterface.BUTTON_POSITIVE:
	            	//mPositiveButtonText = text;
	                mButtonPositiveMessage = msg;
	                break;
	                
	            case DialogInterface.BUTTON_NEGATIVE:
	            	//mNegativeButtonText = text;
	                mButtonNegativeMessage = msg;
	                break;
	                
	            case DialogInterface.BUTTON_NEUTRAL:
	            	//mNeutralButtonText = text;
	                mButtonNeutralMessage = msg;
	                break;
	                
	            default:
	                throw new IllegalArgumentException("Button does not exist");
	        }
	    }
		
		public Builder setView(View v){
			mViewContent = v;
			return this;
		}
		
		public Builder setItems(int itemsId, OnClickListener listener){
			mItemArray = mContext.getResources().getStringArray(itemsId);
			mItemClickListener = listener;
			return this;
		}
		
		public Builder setItems(String[] items, OnClickListener listener){
			mItemArray = items;
			mItemClickListener = listener;
			return this;
		}
		
        public Builder setAdapter(final ListAdapter adapter, final OnClickListener listener) {
            mListAdapter = adapter;
            mItemClickListener = listener;
            return this;
        }
        
        public Builder setSingleChoiceItems(String[] items, int checkedItem, final OnClickListener listener) {
            mItemArray = items;
            mItemClickListener = listener;
            mSelectedItemtIndex = checkedItem;
            mIsSingleChoice = true;
            return this;
        } 
        
        public Builder setCancelable(boolean cancelable){
        	mCancelable = cancelable;
        	return this;
        }
        
        public Builder setOnCancelListener(OnCancelListener listener){
        	mOnCancelListener = listener;
        	return this;
        }
        
        /**
         * 设置布局资源
         * @param layout
         */
        public void setLayoutResource(int layout){
        	mLayout = layout;
        }
        
    	@SuppressWarnings("unused")
        public CustomAlertDialog create(int type){
            this.type = type;
        	LayoutInflater infl = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	View layout = infl.inflate(mLayout == 0 ? R.layout.custom_dialog : mLayout, null);
        	if (layout == null){
        		return null;
        	}
        	mDialogTemplate = layout;
        	
			setupTitle();
        	setupMessage();
        	boolean hasList = setupList();
        	boolean hasButton = setupButton();
        	boolean hasView = setupView();
        	
        	if (hasList){
        		((LinearLayout.LayoutParams) mDialogTemplate.findViewById(R.id.customPanel).getLayoutParams()).weight = 0;
				mDialogTemplate.findViewById(R.id.contentPanel).setVisibility(View.VISIBLE);
        	}
        	
        	if (hasView){
        		LinearLayout content = (LinearLayout) mDialogTemplate.findViewById(R.id.contentPanel);
        		content.removeAllViews();
        		mDialogTemplate.findViewById(R.id.contentPanel).setVisibility(View.GONE);
        	}else{
        		mDialogTemplate.findViewById(R.id.customPanel).setVisibility(View.GONE);
        	}
        	
        	if (!hasButton){
        		View buttonPanel = mDialogTemplate.findViewById(R.id.buttonPanel);
        		buttonPanel.setVisibility(View.GONE);
				((ViewGroup) mDialogTemplate).removeView(buttonPanel);
			}
			View dialog_generic_view_title_line = (View) mDialogTemplate.findViewById(R.id.dialog_generic_view_title_line);
			if (color != 0) {
				dialog_generic_view_title_line.setVisibility(View.VISIBLE);
				dialog_generic_view_title_line.setBackgroundColor(color);
			}

        	CustomAlertDialog customAlertDialog = new CustomAlertDialog(mContext, R.style.CustomDialog);
        	customAlertDialog.setContentView(mDialogTemplate);
        	mDialog = customAlertDialog;
        	mDialog.setCancelable(mCancelable);
        	mDialog.setOnKeyDown(this);
        	mDialog.setType(type);
            mDialog.setOnDismissListener(this);
        	mHandler = new ButtonHandler(mDialog);
        	setupButtonListener();
        	if (mOnCancelListener != null){
        		mDialog.setOnCancelListener(mOnCancelListener);
        	}
        	return mDialog;
        }
        
        private void setupButtonListener() {
        	setButton(BUTTON_POSITIVE, mPositiveButtonOnClickListener, mButtonPositiveMessage);
        	setButton(BUTTON_NEGATIVE, mNegativeButtonOnClickListener, mButtonNegativeMessage);
        	setButton(BUTTON_NEUTRAL, mNeutralButtonOnClickListener, mButtonNeutralMessage);
		}

		private boolean setupView() {
        	if (mViewContent == null){
        		return false;
        	}else{
        		FrameLayout custom = (FrameLayout) mDialogTemplate.findViewById(R.id.custom);
        		custom.addView(mViewContent, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
        	}
			return true;
		}

		private boolean setupButton() {
        	int BIT_BUTTON_POSITIVE = 1;
            int BIT_BUTTON_NEGATIVE = 2;
            int BIT_BUTTON_NEUTRAL = 4;
            int whichButtons = 0;
            mBtnPositive = (Button) mDialogTemplate.findViewById(R.id.btn_check);
			if (color != 0) {
				mBtnPositive.setBackgroundColor(color);
			}
            mBtnPositive.setOnClickListener(mButtonHandler);

            if (TextUtils.isEmpty(mPositiveButtonText)) {
            	mBtnPositive.setVisibility(View.GONE);
            } else {
                mBtnPositive.setText(mPositiveButtonText);
                mBtnPositive.setVisibility(View.VISIBLE);
                whichButtons = whichButtons | BIT_BUTTON_POSITIVE;
            }

            mBtnNegative = (Button) mDialogTemplate.findViewById(R.id.btn_departure);
            mBtnNegative.setOnClickListener(mButtonHandler);

            if (TextUtils.isEmpty(mNegativeButtonText)) {
                mBtnNegative.setVisibility(View.GONE);
            } else {
                mBtnNegative.setText(mNegativeButtonText);
                mBtnNegative.setVisibility(View.VISIBLE);

                whichButtons = whichButtons | BIT_BUTTON_NEGATIVE;
            }

            mBtnNeutral = (Button) mDialogTemplate.findViewById(R.id.button3);
            mBtnNeutral.setOnClickListener(mButtonHandler);

            if (TextUtils.isEmpty(mNeutralButtonText)) {
                mBtnNeutral.setVisibility(View.GONE);
            } else {
                mBtnNeutral.setText(mNeutralButtonText);
                mBtnNeutral.setVisibility(View.VISIBLE);

                whichButtons = whichButtons | BIT_BUTTON_NEUTRAL;
            }

            /*
             * 如果只有一个按钮则将其充满50%的空间，并居中
             */
            if (whichButtons == BIT_BUTTON_POSITIVE) {
                centerButton(mBtnPositive);
            } else if (whichButtons == BIT_BUTTON_NEGATIVE) {
                centerButton(mBtnNeutral);
            } else if (whichButtons == BIT_BUTTON_NEUTRAL) {
                centerButton(mBtnNeutral);
            }
            
            return whichButtons != 0;
		}
        
        private void centerButton(Button button) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button.getLayoutParams();
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.weight = 0.5f;
            button.setLayoutParams(params);
            View leftSpacer = mDialogTemplate.findViewById(R.id.leftSpacer);
            leftSpacer.setVisibility(View.VISIBLE);
            View rightSpacer = mDialogTemplate.findViewById(R.id.rightSpacer);
            rightSpacer.setVisibility(View.VISIBLE);
        }

		private boolean setupList() {
			int layout = mIsSingleChoice ? android.R.layout.select_dialog_singlechoice
					: android.R.layout.select_dialog_item;
        	ListAdapter adapter;
        	if (mListAdapter == null){
        		if (mItemArray == null){
        			return false;
        		}
        		adapter = new ArrayAdapter<String>(mContext, layout, android.R.id.text1, mItemArray){
        			
        			@Override
        			public View getView(int position, View convertView,
        					ViewGroup parent) {
        				View v = super.getView(position, convertView, parent);
        				if (v instanceof TextView){
        					TextView txt = (TextView) v;
        					txt.setMinimumHeight(DensityUtil.dp2px(6));
        					txt.setTextSize(DensityUtil.dp2px(13));
        				}
        				return v;
        			}
        		};
        	}else{
        		adapter = mListAdapter;
        	}
        	createListView(adapter);
			return true;
		}
        

		private void createListView(ListAdapter adapter) {
			LinearLayout content = (LinearLayout) mDialogTemplate.findViewById(R.id.contentPanel);
			content.removeView(mDialogTemplate.findViewById(R.id.scrollView));
			ListView lst = new ListView(mContext);
			mListView = lst;
			int choiceMode = mIsSingleChoice ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE;
			mListView.setChoiceMode(choiceMode);
			mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			mListView.setCacheColorHint(Color.TRANSPARENT);
			content.addView(mListView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
			content.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 0, 1.0f));
			mListView.setAdapter(adapter);
			mListView.setItemChecked(mSelectedItemtIndex, true);
			if (mItemClickListener != null){
				mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						mItemClickListener.onClick(mDialog, arg2);
						if (!mIsSingleChoice){
							mDialog.dismiss();
						}
					}
				});
					
			}
		}

		private boolean setupMessage() {
        	if (!TextUtils.isEmpty(mMessage)){
        		TextView txtMessage = (TextView) mDialogTemplate.findViewById(R.id.message);
				txtMessage.setTextSize(messageTextSize);
        		txtMessage.setText(mMessage);
        		return true;
        	}else{
        		View messagePanel = mDialogTemplate.findViewById(R.id.contentPanel);
        		messagePanel.setVisibility(View.GONE);
        	}
			return false;
		}

		private boolean setupTitle() {
        	if (!TextUtils.isEmpty(mTitle)){
        		TextView txtTitle = (TextView) mDialogTemplate.findViewById(R.id.title_message);
				if (color != 0) {
					txtTitle.setTextColor(color);
				}
        		txtTitle.setText(mTitle);
        		return true;
        	}else{
        		View titlePanel = mDialogTemplate.findViewById(R.id.title_panel);
        		titlePanel.setVisibility(View.GONE);
        	}
			return false;
		}

		public CustomAlertDialog show(){
			CustomAlertDialog dialog = create(mDialog.getType());
			dialog.show();
        	return dialog;
        }

		public boolean keyDown(DialogInterface dialog, int keyCode, KeyEvent event, int type) {
			if (onKeyDown != null) {
				return onKeyDown.onKeyDownCall(dialog, keyCode, event, type);
			}
			return false;
		}
//        public void setOnDismissListener(OnDismissListener listener) {
//            if (onDismisslistener != null) {
//                onDismisslistener.setOnDismissListener(listener);
//            }
//        }

		/**
		 * 按键监听器
		 * @param onKeyDown
		 */
		public void setOnKeyDownListener(OnKeyDown onKeyDown) {
			this.onKeyDown = onKeyDown;
		}

        /**
         * 监听提示框取消
         * @param onDismiss
         */
        public void setOnDismissListener(OnDismiss onDismiss) {
            this.onDismiss = onDismiss;
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            if (onDismiss != null) {
                onDismiss.OnDismissCall(dialog, type);
            }
        }

//        /**
//         * 按提示框取消监听
//         * @param onDismisslistener
//         */
//        public void setOnDismissListener(OnDismissListener onDismisslistener) {
//            this.onDismisslistener = onDismisslistener;
//        }

		public interface OnKeyDown {
			/**
			 * 按键监听回调
			 * @param dialog
			 * @param keyCode
			 * @param event
			 */
			boolean onKeyDownCall(DialogInterface dialog, int keyCode, KeyEvent event, int type);
		}

	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		return builder.keyDown(dialog, keyCode, event, getType());
	}

    public void setOnKeyDown(Builder builder) {
		this.builder = builder;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}


}
