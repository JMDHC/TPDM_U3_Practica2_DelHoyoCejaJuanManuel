package mx.edu.ittepic.tpdm_u3_practica2_delhoyocejajuanmanuel;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Main2Activity extends AppCompatActivity {

    FirebaseAuth fba;
    EditText id,pass;
    Button crear,iniciar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        id=findViewById(R.id.correo);
        pass=findViewById(R.id.contrasena);
        crear=findViewById(R.id.inscribirte);
        iniciar=findViewById(R.id.entrar);
        fba=FirebaseAuth.getInstance();

        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearUsuario();
            }
        });
        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarSesion();
            }
        });

    }

    public void cerrarSesion(){
        fba.signOut();
    }
    public void crearUsuario(){
        fba.createUserWithEmailAndPassword(id.getText().toString(),pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
            public void onComplete(@NonNull Task<AuthResult> task){
                if (task.isSuccessful()){
                    Toast.makeText(Main2Activity.this, "Se creo el usuario", Toast.LENGTH_SHORT).show();
                    fba.getCurrentUser().sendEmailVerification();
                }
                else{
                    Toast.makeText(Main2Activity.this, "Error al crear usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void iniciarSesion(){
        fba.signInWithEmailAndPassword(id.getText().toString(),pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
            public void onComplete(@NonNull Task<AuthResult> task){
                if (task.isSuccessful()){
                    if (fba.getCurrentUser().isEmailVerified()){
                        startActivity(new Intent(Main2Activity.this,Main3Activity.class));
                    }
                    else{
                        AlertDialog.Builder alerta=new AlertDialog.Builder(Main2Activity.this);
                        alerta.setTitle("Verifique Email")
                                .setMessage("Usted no ha verificado su email, desea reenviar el email?")
                                .setPositiveButton("SI", new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog,int witch){
                                        fba.getCurrentUser().sendEmailVerification();
                                        Toast.makeText(Main2Activity.this, "Correo enviado", Toast.LENGTH_SHORT).show();
                                        cerrarSesion();
                                    }
                                })
                                .setNegativeButton("NO",new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog,int witch){
                                        cerrarSesion();
                                    }
                                }).show();
                    }
                }
            }
        });
    }
}
