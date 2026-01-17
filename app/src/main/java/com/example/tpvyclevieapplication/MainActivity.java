package com.example.tpvyclevieapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MeteoAppLifecycle";

    private EditText editTextVille;
    private ListView listViewMeteo;
    List<MeteoItem> data = new ArrayList<>();
    private MeteoListModel model;
    private ImageButton buttonOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextVille = findViewById(R.id.editTextVille);
        listViewMeteo = findViewById(R.id.listViewMeteo);
        buttonOK = findViewById(R.id.buttonOK);

        model = new MeteoListModel(this, R.layout.list_item_layout, data);
        listViewMeteo.setAdapter(model);

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ville = editTextVille.getText().toString().trim();
                if (ville.isEmpty()) {
                    editTextVille.setError("Entrez une ville");
                    return;
                }
                // On lance la recherche
                searchWeatherData(ville);
            }
        });
    }

    private void searchWeatherData(String ville) {
        Log.i(LOG_TAG, "Lancement de la recherche pour la ville : " + ville);
        // On vide la liste précédente
        data.clear();
        model.notifyDataSetChanged();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        // ATTENTION : Remplacez par votre propre clé API si celle-ci ne fonctionne plus.
        String url = "https://api.openweathermap.org/data/2.5/forecast?q="
                + ville + "&appid=a4578e39643716894ec78b28a71c7110";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i(LOG_TAG, "Réponse reçue du serveur.");
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("list");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                MeteoItem meteoItem = new MeteoItem();
                                JSONObject d = jsonArray.getJSONObject(i);

                                // Date
                                Date date = new Date(d.getLong("dt") * 1000);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy 'à' HH:mm", Locale.FRENCH);
                                String dateString = sdf.format(date);

                                // Températures
                                JSONObject main = d.getJSONObject("main");
                                int tempMin = (int) (main.getDouble("temp_min") - 273.15);
                                int tempMax = (int) (main.getDouble("temp_max") - 273.15);

                                // Pression et humidité
                                int pression = main.getInt("pressure");
                                int humidity = main.getInt("humidity");

                                // Image (météo principale)
                                JSONArray weather = d.getJSONArray("weather");
                                String weatherMain = weather.getJSONObject(0).getString("main");

                                // Remplissage de l'objet MeteoItem
                                meteoItem.tempMax = tempMax;
                                meteoItem.tempMin = tempMin;
                                meteoItem.pression = pression;
                                meteoItem.humidite = humidity;
                                meteoItem.date = dateString;
                                meteoItem.image = weatherMain;

                                data.add(meteoItem);
                            }
                            // On notifie l'adapter que les données ont changé
                            model.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "Erreur de parsing JSON", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, "Erreur de connexion !", error);
                        Toast.makeText(MainActivity.this, "Erreur de connexion ou ville non trouvée", Toast.LENGTH_LONG).show();
                    }
                });

        queue.add(stringRequest);
    }

    // --- Implémentation du cycle de vie (Partie 10) ---

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "Application visible (onStart)", Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "onStart() appelé");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "Application prête (onResume)", Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "onResume() appelé");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "L'application perd le focus (onPause)", Toast.LENGTH_SHORT).show();
        Log.w(LOG_TAG, "onPause() appelé - L'application est mise en pause.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        new AlertDialog.Builder(this)
                .setTitle("Information")
                .setMessage("L'application est maintenant en arrière-plan (onStop).")
                .setPositiveButton(android.R.string.ok, null)
                .show();
        Log.w(LOG_TAG, "onStop() appelé");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new AlertDialog.Builder(this)
                .setTitle("Redémarrage")
                .setMessage("Souhaitez-vous continuer l'utilisation ? (onRestart)")
                .setPositiveButton("Continuer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // L'utilisateur continue
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // L'utilisateur annule
                        finish(); // Optionnel: fermer l'app
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
        Log.i(LOG_TAG, "onRestart() appelé");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(LOG_TAG, "onDestroy() appelé - Fermeture définitive de l'application.");
        // Ici, on pourrait libérer des ressources si nécessaire.
    }
}
