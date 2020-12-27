package mc.apps.demo0;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import mc.apps.demo0.model.Profil;
import mc.apps.demo0.model.User;
import mc.apps.demo0.model.UserRepository;

public class StartActivity extends AppCompatActivity {
    ImageView logo;
    ConstraintLayout root, login_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getSupportActionBar().hide(); //masquer barre d'actions/menus

        /**
         * Lancer animation logo + apparition form login
         */
        startAnimation();

        /**
         * Gestion Boutons + liens
         */
        handleActions();
    }

    @Override
    public void onBackPressed() {
        confirmExit();
    }

    private void confirmExit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Vous êtes sûr de vouloir quitter?");

        alertDialogBuilder.setPositiveButton("OUI", (dialog, which) -> finish());
        alertDialogBuilder.setNegativeButton("NON", null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void handleActions() {
        Button btnsignin = findViewById(R.id.btnsignin);
        TextView linksignup = findViewById(R.id.txtlinksignup);
        TextView linkforgotten = findViewById(R.id.txtlinkfortgotten);

        View.OnClickListener ecouteur = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.btnsignin) {
                    handleLogin();          // gestion login
                }else if(view.getId()==R.id.txtlinksignup) {
                    Toast.makeText(StartActivity.this, "TODO : création compte..", Toast.LENGTH_SHORT).show();
                   /* Intent intent = new Intent(StartActivity.this, SignupActivity.class);
                    startActivity(intent);*/
                }else {
                    Toast.makeText(StartActivity.this, "TODO : récup mot de passe oublié..", Toast.LENGTH_SHORT).show();
                   /* Intent intent = new Intent(StartActivity.this, ForgottenActivity.class);
                    startActivity(intent);*/
                }
            }
        };

        //liaison gestionnaire evenement (click) bouton + links
        btnsignin.setOnClickListener(ecouteur);
        linksignup.setOnClickListener(ecouteur);
        linkforgotten.setOnClickListener(ecouteur);
    }

    private void handleLogin() {
        EditText login = findViewById(R.id.edtlogin);
        EditText password = findViewById(R.id.edtpassword);

        String login_txt = login.getText().toString();
        String password_txt = password.getText().toString();

        UserRepository  repository = new UserRepository();
        User user = repository.find(login_txt, password_txt);

        if(user!=null){

            Toast.makeText(StartActivity.this, "Bienvenue "+user.getPrenom(), Toast.LENGTH_SHORT).show();

            openActivity(user); //ouvrir nvlle fenêtre / profil utilisateur connecté!

            /*
            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
            intent.putExtra("user", user.getPrenom()+" "+user.getNom());
            startActivity(intent);*/
        }else{
            Toast.makeText(StartActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
        }

    }

    private void openActivity(User user) {

        Profil profil = user.getProfil();

        Class<?> class_activity;
        switch (profil){
            case Administrateur:
                class_activity = AdminActivity.class;
                break;
            case Superviseur:
                class_activity = SupervisorActivity.class;
                break;
            default:
                class_activity = TechnicianActivity.class;
        }

        Intent intent = new Intent(StartActivity.this, class_activity);
        intent.putExtra("user", (Serializable) user);
        startActivity(intent);

    }

    private void startAnimation() {

        logo = findViewById(R.id.logo);
        root = findViewById(R.id.root);
        login_root = findViewById(R.id.login_root);

       /* btn1 = findViewById(R.id.btnGoogle);*/
        login_root.setAlpha(0f);

        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator fadeAnim = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f);
        fadeAnim.setDuration(2500);
        fadeAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                //((ObjectAnimator)animation).getTarget();
                animateLogo();
            }
        });

        animatorSet.play(fadeAnim);
        animatorSet.start();
    }

    private void animateLogo() {
        ConstraintSet finishingConstraintSet= new ConstraintSet();
        finishingConstraintSet.clone(getApplicationContext(), R.layout.activity_start_final);

        TransitionManager.beginDelayedTransition(root);
        finishingConstraintSet.applyTo(root);

        logo.setImageDrawable(getResources().getDrawable(R.drawable.ic_app_logo_red,null));

        ValueAnimator fade1Anim = ObjectAnimator.ofFloat(login_root, "alpha", 0f, 1f).setDuration(1500);
        //ValueAnimator fade2Anim =ObjectAnimator.ofFloat(btn2, "alpha", 0f, 1f).setDuration(200);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(fade1Anim); //.before(fade2Anim);
        animatorSet.start();
    }

}