package com.example.parcrestaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoixTable extends AppCompatActivity {
    ConnectionClass connectionClass;

    String user, password, base, ip;

    final Context co = this;


    ArrayList<String> datacode, data;
    String datedebut = "", datefin = "";
    DatePicker datePicker;
    GridView gridTable;
    String Admin = "", CodeSearch = "";

    Spinner spincommercial, spinObjectif;

    TextView txtdatedebut, txtdatefin, txtclient, txtimpaye, txtpreavis;
    ProgressBar progressBar;
    String CodeRepresentant, NumObjectif;
    String queryTable = "",queryObjectifGlobal="";
    RadioButton radioGlobal,radioDetaille;
    ArrayList<String> data_objectif;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_table);

        /// CONNECTION BASE
        SharedPreferences pref = getSharedPreferences("usersessionsql", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        user = pref.getString("user", user);
        ip = pref.getString("ip", ip);
        password = pref.getString("password", password);
        base = pref.getString("base", base);
        ///////////////////////////////////////
        connectionClass = new ConnectionClass();

        gridTable=(GridView)findViewById(R.id.grid_table);
        FillTable fillTable=new FillTable();
        fillTable.execute("");

    }



    public class FillTable extends AsyncTask<String, String, String> {
        String z = "";
        Boolean test = false;
        String tcredit, tdebit;

        List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {



        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(String r) {

            //   Toast.makeText(getApplicationContext(), r, Toast.LENGTH_SHORT).show();

            String[] from = {"NumeroTable", "Ouvert", "NumeroBonLivraisonVente", "D", "E"};
            int[] views = {R.id.numtable, R.id.txt_ouvert };
            final SimpleAdapter ADA = new SimpleAdapter(getApplicationContext(),
                    prolist, R.layout.item_table, from,
                    views);


            final BaseAdapter baseAdapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return prolist.size();
                }

                @Override
                public Object getItem(int position) {
                    return null;
                }

                @Override
                public long getItemId(int position) {
                    return 0;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    final LayoutInflater layoutInflater = LayoutInflater.from(co);
                    convertView = layoutInflater.inflate(R.layout.item_table, null);
                    final TextView txt_numtable = (TextView) convertView.findViewById(R.id.txt_num_table);

                    final TextView txt_ouvert = (TextView) convertView.findViewById(R.id.txt_ouvert);
               
             final CardView cardView = (CardView) convertView.findViewById(R.id.btn_item);
                    final HashMap<String, Object> obj = (HashMap<String, Object>) ADA
                            .getItem(position);
                    String testOuvert=     (String) obj.get("Ouvert");
                  final String st_num=     (String) obj.get("NumeroTable");
                final   String st_NumeroBonLivraisonVente=     (String) obj.get("NumeroBonLivraisonVente");

                    txt_numtable.setText(st_num);

                    try {


                        if (testOuvert.equals("True")) {
                             cardView.setCardBackgroundColor(Color.parseColor("#993300"));
                             txt_ouvert.setText("ouverte");
                            txt_ouvert.setTextColor(Color.parseColor("#ffffff"));
                            txt_numtable.setTextColor(Color.parseColor("#ffffff"));
                        }else{
                            txt_ouvert.setText("Fermée");

                        }


                    } catch (Exception e) {
                        Log.e("ere", e.toString());
                    }

                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(getApplicationContext(),Home.class);
                            intent.putExtra("NumeroBonLivraisonVente",st_NumeroBonLivraisonVente);
                            intent.putExtra("NumeroTable",st_num);
                            startActivity(intent);

                        }
                    });
                    return convertView;
                }
            };


            gridTable.setAdapter(baseAdapter);


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN(ip, password, user, base);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

 



                    queryTable="select TableRestaurant.NumeroTable ,isnull(( select isnull(NumeroBonLivraisonVente,'') \n" +
                            " from TempTicketTemporaire where TempTicketTemporaire.NumeroTable =TableRestaurant.NumeroTable and  NumeroEtat='E05'),'')\n" +
                            "as NumeroBonLivraisonVente ,\n" +
                            "case when  NumeroEtat='E05' then  'True'  \n" +
                            "else 'False' \n" +
                            "end      as 'Ouverte'  \n" +
                            "from TableRestaurant \n" +
                            "left outer join TempTicketTemporaire on TableRestaurant.NumeroTable = TempTicketTemporaire.NumeroTable \n" +
                            "order by  CONVERT  (int, TableRestaurant.NumeroTable)  ";

                    PreparedStatement ps = con.prepareStatement(queryTable);
                    Log.e("query", queryTable);

                    ResultSet rs = ps.executeQuery();
                    z = "e";

                    while (rs.next()) {

                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("NumeroTable", rs.getString("NumeroTable"));

                        datanum.put("Ouvert", rs.getString("Ouverte"));
                        datanum.put("NumeroBonLivraisonVente", rs.getString("NumeroBonLivraisonVente"));
                        prolist.add(datanum);


                        test = true;


                        z = "succees";
                    }


                }
            } catch (SQLException ex) {
                z = "tablelist" + ex.toString();

            }
            return z;
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        FillTable fillTable=new FillTable();
        fillTable.execute("");
    }
}
