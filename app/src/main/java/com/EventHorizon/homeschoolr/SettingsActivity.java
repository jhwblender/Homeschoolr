package com.EventHorizon.homeschoolr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity implements TaskListener {
    Auth auth;
    Functions functions;
    TextView emailView;
    TextView nameView;
    TextView familyNameView;
    TextView copyright;
    Switch adSwitch;
    boolean adChanged = false;
    TableRow enterEmailDialog;
    EditText inviteEmail;
    EditText childName;
    String email;
    LinearLayout inviteContainer;
    LinearLayout memberContainer;
    LinearLayout childrenWithoutAccountContainer;

    Person user;
    Family family;

    ArrayList<String> invited;
    ArrayList<Person> members;

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

        copyright = findViewById(R.id.copyright);
        String copyrightText = copyright.getText()+" "+Calendar.getInstance().get(Calendar.YEAR);
        copyright.setText(copyrightText);

        nameView = findViewById(R.id.name);
        emailView = findViewById(R.id.email);
        familyNameView = findViewById(R.id.familyName);
        enterEmailDialog = findViewById(R.id.EnterEmailDialog);
        inviteEmail = findViewById(R.id.inviteEmail);
        inviteContainer = findViewById(R.id.inviteContainer);
        memberContainer = findViewById(R.id.memberContainer);
        childrenWithoutAccountContainer = findViewById(R.id.childrenWithoutAccountsContainer);
        childName = findViewById(R.id.childName);

        adSwitch = findViewById(R.id.adSwitch);
        adSwitch.setChecked(user.getShowAds());

        emailView.setText(email);
        ArrayList<Person> users = family.getMembers(this);

        familyNameView.setText(family.getFamilyName());
        nameView.setText(family.getMember(this, auth.getEmail()).getName());
        if(!family.getMember(this, auth.getEmail()).getIsParent()){
            findViewById(R.id.invitedFamilyMembersTitle).setVisibility(View.GONE);
            findViewById(R.id.invitedFamilyMembers).setVisibility(View.GONE);
            findViewById(R.id.childrenWithoutAccountsTitle).setVisibility(View.GONE);
            findViewById(R.id.childrenWithoutAccounts).setVisibility(View.GONE);
        }

        if(invited == null)
            populateInvites();
        if(members == null)
            populateMembers();
    }

    @Override
    public void onBackPressed() {
        if(adChanged || childrenChange)
            functions.goToActivity(CalendarActivity.class);
        else
            super.onBackPressed();
    }

    //Logout button clicked
    public void logout(View view){
        family.unsave(this);
        auth.signOut();
        functions.goToActivity(LoginActivity.class);
    }

    //Ad management starts here
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
    }; //end ad management

    public void paypalClicked(View view){
        goToUrl("https://www.paypal.me/jhwblender");
    }
    public void patreonClicked(View view){
        goToUrl("https://www.patreon.com/jhwblender");
    }
    public void facebookClicked(View view){
        goToUrl("https://www.facebook.com/groups/homeschoolr");
    }
    public void mailClicked(View view){
        String[] addresses = {auth.getEmail(),"jhwblender@gmail.com"};
        String subject = "Help with Homeschoolr";
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    //Delete Account button clicked
    public void deleteAccount(View view){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.deleteConfirmation))
                .setPositiveButton(getString(R.string.yes), deleteAccountPopup)
                .setNegativeButton(getString(R.string.no), deleteAccountPopup).show();
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
                Person.unsave(context);
                family.removeMember(auth.getEmail(),context);
            }else
                functions.showMessage(getString(R.string.deleteCancel),false);
        }
    };

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

    public void addInvite(String email){
        getLayoutInflater().inflate(R.layout.invite_row, inviteContainer, true);
        ConstraintLayout layout = (ConstraintLayout) inviteContainer.getChildAt(inviteContainer.getChildCount() - 1);
        TableRow inviteRow = (TableRow) layout.getChildAt(0);
        ((ViewGroup)inviteRow.getParent()).removeView(inviteRow);
        inviteContainer.addView(inviteRow);
        TextView emailView = (TextView) inviteRow.getChildAt(0);
        emailView.setText(email);
    }

    public void deleteInvite(View view){
        String email = ((TextView)((TableRow)view.getParent()).getChildAt(0))
                .getText().toString();
        family.removeInvite(email, this);
        ((ViewGroup)(view.getParent().getParent())).removeView((View)view.getParent());
    }

    public void populateInvites(){
        invited = family.getInviteEmailList();
        for(int i = 0; i < invited.size(); i++) {
            addInvite(invited.get(i));
        }
    }

    public void addChildWithoutAccount(View view){
        childrenChange = true;
        String name = this.childName.getText().toString();
        addChildWithoutAccount(name);
        childName.setText("");
        functions.hideKeyboard();
        childName.clearFocus();
        String fakeEmail = Functions.namesToFakeEmail(name, family.getFamilyName());
        Person child = new Person(fakeEmail,false,name,family.getFamilyName()
                ,true,true);
        child.save(this);
        functions.showMessage(name + " " + getString(R.string.added));
        family.addMember(fakeEmail,this);
    }
    public void addChildWithoutAccount(String name){
        getLayoutInflater().inflate(R.layout.child_without_account_row
                , childrenWithoutAccountContainer, true);
        ConstraintLayout layout = (ConstraintLayout) childrenWithoutAccountContainer
                .getChildAt(childrenWithoutAccountContainer.getChildCount() - 1);
        TableRow childRow = (TableRow) layout.getChildAt(0);
        ((ViewGroup)childRow.getParent()).removeView(childRow);
        childrenWithoutAccountContainer.addView(childRow);
        TextView childNameView = (TextView) childRow.getChildAt(0);
        childNameView.setText(name);
    }


    Person childDeleting;
    boolean childrenChange = false;
    View deleteRow;
    public void deleteChild(View view){
        deleteRow = view;
        String childToDelete = ((TextView)((ViewGroup)view.getParent()).getChildAt(0))
                .getText().toString();;
        childDeleting = family.getMemberByName(this, childToDelete);
        if(childDeleting.getIsNoAccountChild()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.deleteConfirmation))
                    .setPositiveButton(getString(R.string.yes), deleteChildPopup)
                    .setNegativeButton(getString(R.string.no), deleteChildPopup).show();
        }
    }

    //Verification for account deletion
    DialogInterface.OnClickListener deleteChildPopup = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which==DialogInterface.BUTTON_POSITIVE) {
                ((ViewGroup)(deleteRow.getParent().getParent())).removeView((View)deleteRow
                        .getParent());
                childDeleting.deleteAccount(childDeleting.getEmail(),context);
                family.removeMember(childDeleting.getEmail(),context);
                childrenChange = true;
            }else
                functions.showMessage(getString(R.string.deleteCancel),false);
        }
    };

    public void populateMembers(){
        members = family.getMembers(this);
        for(int i = 0; i < members.size(); i++) {
            if(members.get(i).getIsNoAccountChild())
                addChildWithoutAccount(members.get(i).getName());
            else
                addMember(members.get(i).getName(), members.get(i).getIsParent());
        }
    }
    private void addMember(String memberName, boolean parent){
        getLayoutInflater().inflate(R.layout.member_row, memberContainer, true);
        ConstraintLayout layout = (ConstraintLayout) memberContainer.getChildAt(memberContainer.getChildCount() - 1);
        TableRow inviteRow = (TableRow) layout.getChildAt(0);
        ((ViewGroup)inviteRow.getParent()).removeView(inviteRow);
        memberContainer.addView(inviteRow);
        TextView emailView = (TextView) inviteRow.getChildAt(0);
        emailView.setText(memberName);
        TextView relationshipView = (TextView) inviteRow.getChildAt(1);
        relationshipView.setText((parent)? getString(R.string.parent) : getString(R.string.child));
    }

    @Override
    public void authResult(TaskName result) {
        switch(result){
            case DB_DELETE_USER_SUCCESSFUL:
                if(!childrenChange)
                    auth.deleteAccount();
                break;
            case AUTH_DELETE_ACCOUNT_SUCCESSFUL:
                functions.goToActivity(LoginActivity.class);
        }
    }
}
