package com.google.linkedinapi.android;

import java.util.HashMap;
import java.util.List;

import org.apache.http.client.methods.HttpPost;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.enumeration.SearchParameter;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.schema.People;
import com.google.code.linkedinapi.schema.Person;
import com.google.linkedinapi.android.LinkedinDialog.OnVerifyListener;

/**
 * @author Vivek Kumar Srivastava
 */
public class LinkedInSampleActivity extends Activity
{
	private Button loginButton;
	LinkedInApiClient client;
	HttpPost post;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		loginButton  = (Button) findViewById(R.id.login);
		post = new HttpPost("https://api.linkedin.com/v1/people/~/shares");
//		factory = LinkedInApiClientFactory.newInstance(LinkedinDialog.LINKEDIN_CONSUMER_KEY, LinkedinDialog.LINKEDIN_CONSUMER_SECRET);
		
		loginButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				linkedInLogin();
			}
		});

	}

	/**
	 * Connect IceBreaker with linkedIn.
	 * <br>
	 * i.e. send  linkedIn access token to IceBreaker server.
	 */
	private void linkedInLogin() {
		
		ProgressDialog progressDialog = new ProgressDialog(LinkedInSampleActivity.this);//.show(LinkedInSampleActivity.this, null, "Loadong...");
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(true);
		progressDialog.show();
		
		LinkedinDialog dialog = new LinkedinDialog(LinkedInSampleActivity.this, progressDialog);
		dialog.show();
		
		//set call back listener to get oauth_verifier value
		dialog.setVerifierListener(new OnVerifyListener() {
			@Override
			public void onVerify(String verifier) {
				try {
					Log.i("LinkedinSample", "verifier: " + verifier);

					LinkedInAccessToken accessToken = LinkedinDialog.oAuthService.getOAuthAccessToken(LinkedinDialog.liToken, verifier); 
					Log.i("LinkedinSample", "ln_access_token: " + accessToken.getToken());
					Log.i("LinkedinSample", "ln_access_token: " + accessToken.getTokenSecret());
					
//					LinkedinDialog.factory.createLinkedInApiClient(accessToken);

					client = LinkedinDialog.factory.createLinkedInApiClient(accessToken);
//					client.postComment(verifier, "New Post...");
					client.updateCurrentStatus("Status update...");
//					client.sendMessage(arg0, arg1, arg2)
					HashMap<SearchParameter, String> map = new HashMap<SearchParameter, String>();
					map.put(SearchParameter.FIRST_NAME, "Rohan");
					People searchedPeople = client.searchPeople(map);
					List<Person> listPersons = searchedPeople.getPersonList();
					for (int i = 0; i < listPersons.size(); i++) {
						System.out.println(" >> "+ listPersons.get(i));
					}
					
//			        client.postNetworkUpdate("LinkedIn app test");
//			        Person p = client.getProfileForCurrentUser();
//			        loginButton.setText(p.getLastName() + ", " + p.getFirstName());
				}
				catch (Exception e) {
					Log.i("LinkedinSample", "error to get verifier");
					e.printStackTrace();
				}
			}
		});
	}
}

