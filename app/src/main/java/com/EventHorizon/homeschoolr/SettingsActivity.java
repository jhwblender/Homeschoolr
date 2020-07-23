package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements TaskListener {
    Auth auth;
    Functions functions;
    TextView emailView;
    TextView nameView;
    TextView familyNameView;
    Switch adSwitch;
    boolean adChanged = false;
    TableRow enterEmailDialog;
    EditText inviteEmail;
    String email;
    LinearLayout inviteContainer;
    LinearLayout memberContainer;


    Person user;
    Family family;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = new Auth(this);
        email = auth.getEmail();
        family = Family.load(this);
        user = family.getMember(this, email);
        functions = new Functions(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        functions.adInit(user);

        nameView = findViewById(R.id.name);
        emailView = findViewById(R.id.email);
        familyNameView = findViewById(R.id.familyName);
        enterEmailDialog = findViewById(R.id.EnterEmailDialog);
        inviteEmail = findViewById(R.id.inviteEmail);
        inviteContainer = findViewById(R.id.inviteContainer);
        memberContainer = findViewById(R.id.memberContainer);
        adSwitch = findViewById(R.id.adSwitch);
        adSwitch.setChecked(user.getShowAds());

        emailView.setText(email);
        ArrayList<Person> users = family.getMembers(this);

        familyNameView.setText(family.getFamilyName());
        nameView.setText(family.getMember(this, auth.getEmail()).getName());
        if(!family.getMember(this, auth.getEmail()).getIsParent()){
            findViewById(R.id.invitedFamilyMembersTitle).setVisibility(View.GONE);
            findViewById(R.id.invitedFamilyMembers).setVisibility(View.GONE);
        }

        populateInvites();
        populateMembers();
    }

    @Override
    public void onBackPressed() {
        if(!adChanged)
            super.onBackPressed();
        else
            functions.goToActivity(CalendarActivity.class);
    }

    //Logout button clicked
    public void logout(View view){
        family.unsave(this);
        auth.signOut();
        functions.goToActivity(LoginActivity.class);
    }

    public void adsToggled(View view){
        Switch adSwitch = (Switch)view;
        if(adSwitch.isChecked()){ //todo add strings
            functions.showMessage(getString(R.string.adsOnThankYou));
            user.setShowAds(true, this);
            functions.adInit(user);
            findViewById(R.id.adView).setVisibility(View.VISIBLE);
            adChanged = true;
        }else{
            AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.adsOffConfirmation))
                    .setPositiveButton(getString(R.string.yes), turnOffAdsPopup)
                    .setNegativeButton(getString(R.string.no), turnOffAdsPopup).show();
        }
    }

    //Verification to turn off ads
    DialogInterface.OnClickListener turnOffAdsPopup = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which==DialogInterface.BUTTON_POSITIVE) {
                user.setShowAds(false, context);
                findViewById(R.id.adView).setVisibility(View.GONE);
                adChanged = true;
            }else{
                functions.showMessage(getString(R.string.adsOnThankYou));
                adSwitch.setChecked(true);
            }
        }
    };

    //Delete Account button clicked
    public void deleteAccount(View view){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.deleteConfirmation))
                .setPositiveButton(getString(R.string.yes), deleteAccountPopup)
                .setNegativeButton(getString(R.string.no), deleteAccountPopup).show();
    }

    public void addInvite(View view){
        String inviteEmailString = inviteEmail.getText().toString();
        if(!functions.checkEmail(inviteEmail.getText().toString()))
            return;
        else {
            inviteEmail.setText("");
            family.inviteMember(inviteEmailString, this);
            addInvite(inviteEmailString);
        }
    }

    //Verification for account deletion
    Context context = this;
    DialogInterface.OnClickListener deleteAccountPopup = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which==DialogInterface.BUTTON_POSITIVE) {
                functions.loadingView(true);
                if(user == null)
                    Log.d("SettingsActivity","USER IS NULL!");
                user.deleteAccount(auth.getEmail(), context); //todo fix acct deletion
                family.removeMember(auth.getEmail(),context);
            }else
                functions.showMessage(getString(R.string.deleteCancel),false);
        }
    };



    public void deleteInvite(View view){
        String email = ((TextView)((TableRow)view.getParent()).getChildAt(0)).getText().toString();
        family.removeInvite(email, this);
        ((ViewGroup)(view.getParent().getParent())).removeView((View)view.getParent());
    }

    public void populateInvites(){
        ArrayList<String> invited = family.getInviteEmailList();
        for(int i = 0; i < invited.size(); i++) {
            addInvite(invited.get(i));
        }
    }
    public void addInvite(String email){

        getLayoutInflater().inflate(R.layout.invite_row, inviteContainer, true);
        ConstraintLayout layout = (ConstraintLayout) inviteContainer.getChildAt(inviteContainer.getChildCount() - 1);
        TableRow inviteRow = (TableRow) layout.getChildAt(0);
        ((ViewGroup)inviteRow.getParent()).removeView(inviteRow);
        inviteContainer.addView(inviteRow);
        TextView emailView = (TextView) inviteRow.getChildAt(0);
        emailView.setText(email);
    }

    public void populateMembers(){
        ArrayList<Person> members = family.getMembers(this);
        for(int i = 0; i < members.size(); i++) {
            addMember(members.get(i).getName());
        }
    }
    private void addMember(String memberName){
        getLayoutInflater().inflate(R.layout.member_row, memberContainer, true);
        ConstraintLayout layout = (ConstraintLayout) memberContainer.getChildAt(memberContainer.getChildCount() - 1);
        TableRow inviteRow = (TableRow) layout.getChildAt(0);
        ((ViewGroup)inviteRow.getParent()).removeView(inviteRow);
        memberContainer.addView(inviteRow);
        TextView emailView = (TextView) inviteRow.getChildAt(0);
        emailView.setText(memberName);
    }

    @Override
    public void authResult(TaskName result) {
        switch(result){
            case DB_DELETE_USER_SUCCESSFUL:
                auth.deleteAccount();
                break;
            case AUTH_DELETE_ACCOUNT_SUCCESSFUL:
                functions.goToActivity(LoginActivity.class);
        }
    }
}
