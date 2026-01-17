package com.example.tpvyclevieapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeteoListModel extends ArrayAdapter<MeteoItem> {
    private List<MeteoItem> listItems;
    private int resource;
    public static Map<String, Integer> images = new HashMap<>();

    static {
        /*
         * Pour que les icônes météo s'affichent, vous devez :
         * 1. Ajouter les images (par exemple, clear.png, clouds.png, rain.png, thunderstorm.png)
         *    dans votre dossier `app/src/main/res/drawable`.
         * 2. Décommenter les lignes ci-dessous.
         *
         * J'ai utilisé "Thunderstorm" car c'est une valeur commune de l'API,
         * le document du TP mentionnait "thunderstormspng" qui est peut-être une coquille.
         * Adaptez les clés (la chaîne de caractères) aux valeurs que vous recevez de l'API.
         */
        images.put("Clear", R.drawable.clear);
        images.put("Clouds", R.drawable.clouds);
        images.put("Rain", R.drawable.rain);images.put("Thunderstorm", R.drawable.thunderstorm);
    }

    public MeteoListModel(Context context, int resource, List<MeteoItem> data) {
        super(context, resource, data);
        this.listItems = data;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }
        ImageView imageView = listItem.findViewById(R.id.imageView);
        TextView textViewTempMax = listItem.findViewById(R.id.textViewTempMAX);
        TextView textViewTempMin = listItem.findViewById(R.id.textViewTempMin);
        TextView textViewPression = listItem.findViewById(R.id.textViewPression);
        TextView textViewHumidite = listItem.findViewById(R.id.textViewHumidite);
        TextView textViewDate = listItem.findViewById(R.id.textViewDate);

        MeteoItem currentItem = listItems.get(position);

        String key = currentItem.image;
        if (key != null && images.containsKey(key)) {
            imageView.setImageResource(images.get(key));
        } else {
            // Si aucune image ne correspond, une image par défaut (l'icône de l'app) sera affichée.
            imageView.setImageResource(R.mipmap.ic_launcher);
        }

        textViewTempMax.setText(String.valueOf(currentItem.tempMax) + " °C");
        textViewTempMin.setText(String.valueOf(currentItem.tempMin) + " °C");
        textViewPression.setText(String.valueOf(currentItem.pression) + " hPa");
        textViewHumidite.setText(String.valueOf(currentItem.humidite) + " %");
        textViewDate.setText(String.valueOf(currentItem.date));

        return listItem;
    }
}
