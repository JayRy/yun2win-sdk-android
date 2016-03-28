/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package y2w.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yun2win.demo.R;

import y2w.manage.CurrentUser;
import y2w.manage.Users;
import y2w.db.DaoManager;
import y2w.service.Back;
import y2w.service.ErrorCode;
import com.y2w.uikit.utils.StringUtil;
import y2w.common.UserInfo;


/**
 * Created by hejie on 2016/3/14.
 * 登录界面
 */
public class LoginActivity extends BaseActivity {
	private static final String TAG = "LoginActivity";
	public static final int REQUEST_CODE_SETNICK = 1;
	private EditText usernameEditText;
	private EditText passwordEditText;

	private boolean progressShow = false;
	private boolean autoLogin = false;

	private String currentUsername;
	private String currentPassword;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 如果用户名密码都有，直接进入主页面

		setContentView(R.layout.activity_login);
		context = this;
		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);

		// 如果用户名改变，清空密码
		usernameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				passwordEditText.setText(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		if(!StringUtil.isEmpty(UserInfo.getAccount())){
			usernameEditText.setText(UserInfo.getAccount());
			if(!StringUtil.isEmpty(UserInfo.getPassWord())){
				passwordEditText.setText(UserInfo.getPassWord());
				autoLogin(UserInfo.getAccount(),UserInfo.getPassWord());
			}else{
				passwordEditText.requestFocus();
			}
		}
		//创建数据库
		DaoManager.getInstance(this);

	}

	/**
	 * 登录
	 * 
	 * @param view
	 */
	public void login(View view) {

		currentUsername = usernameEditText.getText().toString().trim();
		currentPassword = passwordEditText.getText().toString().trim();

		if (TextUtils.isEmpty(currentUsername)) {
			Toast.makeText(this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(currentPassword)) {
			Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		autoLogin(currentUsername,currentPassword);
	}

	private void autoLogin(final String account, final String password){
		final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				progressShow = false;
			}
		});
		pd.setMessage(getString(R.string.Is_landing));
		pd.show();
		progressShow = true;
		Users.getInstance().getRemote().login(account, password, new Back.Result<CurrentUser>() {
			@Override
			public void onSuccess(CurrentUser currentUser) {
				if (progressShow == false) {
					return;
				}
				Users.getInstance().getCurrentUser().getRemote().sync(new Back.Callback() {
					@Override
					public void onSuccess() {
						pd.dismiss();
						startActivity(new Intent(LoginActivity.this, MainActivity.class));
						finish();
					}
					@Override
					public void onError(ErrorCode errorCode) {
						pd.dismiss();
					}
				});
			}
			@Override
			public void onError(ErrorCode errorCode,String error) {
				pd.dismiss();
			}
		});
	}



	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
		startActivityForResult(new Intent(this, RegisterActivity.class), 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (autoLogin) {
			return;
		}
	}
}
