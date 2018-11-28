package mx.edu.ittepic.tpdm_u3_practica2_delhoyocejajuanmanuel;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main3Activity extends AppCompatActivity {

    FirebaseAuth fba;
    FirebaseAuth.AuthStateListener asl;
    ListView lista;
    List<Map> alumonosLocal;
    CollectionReference productos;
    FirebaseFirestore baseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        baseDatos=FirebaseFirestore.getInstance();
        productos=baseDatos.collection("alumnos");
        alumonosLocal=new ArrayList<>();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Main3Activity.this,MainActivity.class);
                i.putExtra("operacion",0);
                i.putExtra("clave","");
                startActivity(i);
            }
        });
        lista=findViewById(R.id.listaAlumnos);
        fba=FirebaseAuth.getInstance();
        asl=new FirebaseAuth.AuthStateListener(){
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser usuario=firebaseAuth.getCurrentUser();
                if (usuario==null||!usuario.isEmailVerified()){
                    Toast.makeText(Main3Activity.this, "No esta logueado", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Main3Activity.this,Main2Activity.class));
                }
            }
        };
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if (i >= 0) {
                    AlertDialog.Builder alerta = new AlertDialog.Builder(Main3Activity.this);
                    alerta.setTitle("Modificacion de datos")
                            .setMessage("Desea modificar/eliminar el alumno " + alumonosLocal.get(i).get("nombre").toString())
                            .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int in) {
                                    Intent ventana=new Intent(Main3Activity.this,MainActivity.class);
                                    ventana.putExtra("clave",alumonosLocal.get(i).get("id").toString());
                                    ventana.putExtra("operacion",1);
                                    startActivity(ventana);

                                }
                            })
                            .setNegativeButton("NO",null).show();
                }
            }
        });

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cerrarSesion) {
            fba.signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    protected void onStart(){
        super.onStart();
        fba.addAuthStateListener(asl);
        cargarDatos();

    }
    protected void onStop(){
        super.onStop();
        fba.removeAuthStateListener(asl);
    }

    private void cargarDatos(){
        productos.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if  (queryDocumentSnapshots.size()<=0){
                    mensaje("No hay datos para mostrar");
                    return;
                }
                alumonosLocal=new ArrayList<>();
                for (QueryDocumentSnapshot otro:queryDocumentSnapshots){
                    Alumno producto=otro.toObject(Alumno.class);
                    Map<String,Object> datos=new HashMap<>();
                    datos.put("nombre",producto.getNombre());
                    datos.put("carrera",producto.getCarrera());
                    datos.put("domicilio",producto.getDomicilio());
                    datos.put("id",otro.getId());
                    alumonosLocal.add(datos);
                    llenarLista();
                }
            }
        });
    }

    private void llenarLista(){
        String data[]=new String[alumonosLocal.size()];
        for (int i=0;i<data.length;i++){
            String cad=alumonosLocal.get(i).get("nombre").toString()+"\ncarrera: "+alumonosLocal.get(i).get("carrera").toString()+"\ndomicilio: "+alumonosLocal.get(i).get("domicilio").toString();
            data[i]=cad;
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(Main3Activity.this,android.R.layout.simple_list_item_1,data);
        lista.setAdapter(adapter);
    }

    private void mensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

}
