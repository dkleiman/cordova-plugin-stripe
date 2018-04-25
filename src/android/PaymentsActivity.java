package com.spinyt.stripe;

import android.app.Activity;

import android.os.Bundle;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Typeface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

import android.view.Window;
import android.view.WindowManager;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ProgressBar;

import com.stripe.android.view.CardInputWidget;
import com.stripe.android.Stripe;
import com.stripe.android.model.Token;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.exception.APIConnectionException;
import com.stripe.android.exception.APIException;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.exception.CardException;
import com.stripe.android.exception.InvalidRequestException;
import com.stripe.android.exception.PermissionException;
import com.stripe.android.exception.RateLimitException;
import com.stripe.android.exception.StripeException;

public class PaymentsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String package_name = getApplication().getPackageName();
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(this.getLayout("activity_payments", package_name));

		Window window = this.getWindow();

		// clear FLAG_TRANSLUCENT_STATUS flag:
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

		// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

		Bundle extras = getIntent().getExtras();
		String main_color = extras.getString("main_color");
		String status_bar_color = extras.getString("status_bar_color");
		String title = extras.getString("title");
		String stripe_key = extras.getString("stripe_key");

		// Toolbar
		LinearLayout toolbar = (LinearLayout) this.findViewById(this.getId("toolbar", package_name));
		toolbar.setBackgroundColor(Color.parseColor(main_color));

		TextView titleView = (TextView) findViewById(this.getId("title_text", package_name));
		Typeface custom_font = Typeface.createFromAsset(getAssets(),	"fonts/Lato-Regular.ttf");
		titleView.setTypeface(custom_font);
		titleView.setText(title);

		// finally change the color of the status bar
		window.setStatusBarColor(Color.parseColor(status_bar_color));

		// handle the toolbar back arrow
		ImageButton back_button = (ImageButton) findViewById(this.getId("button_back", package_name));
		back_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(Activity.RESULT_CANCELED, intent);
				finish();
			}
		});

		// handle the add card button
		Button add_card_button = (Button) findViewById(this.getId("add_card", package_name));

		// Styles
		add_card_button.setTypeface(custom_font); // Set font
		Drawable buttonBackground = (Drawable) add_card_button.getBackground().mutate(); // Set background
		buttonBackground.setColorFilter(new PorterDuffColorFilter(Color.parseColor(main_color), PorterDuff.Mode.valueOf("MULTIPLY")));
		add_card_button.setBackground(buttonBackground);

		// spinner
		final ProgressBar spinner = (ProgressBar) findViewById(this.getId("progress_bar", package_name));
		spinner.setVisibility(View.GONE);

		// click handling
		final CardInputWidget mCardInputWidget = (CardInputWidget) findViewById(this.getId("card_input_widget", package_name));
		final Stripe stripe = new Stripe(this, stripe_key);
		final Context mContext = (Context) this;
		add_card_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				spinner.setVisibility(View.VISIBLE);
				// Remember that the card object will be null if the user inputs invalid data.
				Card card = mCardInputWidget.getCard();
				if (card == null) {
					// Do not continue token creation.
					// show error message
					Toast.makeText(mContext,
						"Invalid Card Data",
						Toast.LENGTH_LONG
					).show();
					spinner.setVisibility(View.GONE);
					return;
				}

				stripe.createToken(
					card,
					new TokenCallback() {
						public void onSuccess(Token token) {
							// Send token to your server
							Intent intent = new Intent();
							intent.putExtra("token", token.getId());
							setResult(Activity.RESULT_OK, intent);
							finish();
						}
						public void onError(Exception error) {
							// Show localized error message
							Toast.makeText(mContext,
								error.getLocalizedMessage(),
								Toast.LENGTH_LONG
							).show();
							spinner.setVisibility(View.GONE);
						}
					}
				);
			}
		});

	}

	private int getId(String id, String package_name) {
		return getApplication().getResources().getIdentifier(id, "id", package_name);
	}

	private int getLayout(String id, String package_name) {
		return getApplication().getResources().getIdentifier(id, "layout", package_name);
	}
}