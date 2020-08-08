package com.example.parcrestaurant;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity {
    int i = 0;

    ConnectionClass connectionClass;
    String CodeSociete, NomUtilisateur, CodeLivreur;
    final Context co = this;
    String user, password, base, ip;

    ListView lstpro, listarticle, listzone, listzone2, listcmd;
    String s = "";
    ImageView img;
    Spinner spintable;

    TextView textnumtable, textnum_ticket;
    String codclient = "418", ntable;
    String dates;
    String querylist, proid = "";
    int qt = 0;
    String codefamille, anciencompteur, prefixeticket, anciencompteurticket, incompteurticket, anneeticket;
    String NumeroTable = "", NumeroBonLivraisonVente = "", CodeArticleSelection = "", QteSaisi = "", CodeDepot = "DEP002";
    GridView grid_article, grid_article_lancer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        connectionClass = new ConnectionClass();
        SharedPreferences prefe = getSharedPreferences("usersession", Context.MODE_PRIVATE);
        SharedPreferences.Editor edte = prefe.edit();
        NomUtilisateur = prefe.getString("NomUtilisateur", NomUtilisateur);
        CodeSociete = prefe.getString("CodeSociete", CodeSociete);

        final Intent intent = getIntent();
        NumeroTable = intent.getStringExtra("NumeroTable");
        NumeroBonLivraisonVente = intent.getStringExtra("NumeroBonLivraisonVente");

        SharedPreferences pref = getSharedPreferences("usersessionsql", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        user = pref.getString("user", user);
        ip = pref.getString("ip", ip);
        password = pref.getString("password", password);
        base = pref.getString("base", base);
        textnumtable = (TextView) findViewById(R.id.numtable);
        textnum_ticket = (TextView) findViewById(R.id.num_ticket);

        textnumtable.setText(NumeroTable);
        textnum_ticket.setText(NumeroBonLivraisonVente);

        lstpro = (ListView) findViewById(R.id.listprod);
        listcmd = (ListView) findViewById(R.id.listcmd);
        grid_article_lancer = (GridView) findViewById(R.id.grid_article_lancer);

        FillList fillList = new FillList();
        fillList.execute("");
        FillListArticleCommande fillListArticleCommande = new FillListArticleCommande();
        fillListArticleCommande.execute("");
        FillListArticleCommandeLancer fillListArticleCommandeLancer = new FillListArticleCommandeLancer();
        fillListArticleCommandeLancer.execute("");
        Button btvalider = (Button) findViewById(R.id.btvalider);
        btvalider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LancerCommande lancerCommande = new LancerCommande();
                lancerCommande.execute("");
            }
        });
    }

    public class FillList extends AsyncTask<String, String, String> {
        String z = "", image = "";

        List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onPostExecute(String r) {


            //  Toast.makeText(getApplicationContext(), r, Toast.LENGTH_SHORT).show();

            String[] from = {"A", "B"};
            int[] views = {R.id.txt_num_table};
            final SimpleAdapter ADA = new SimpleAdapter(getApplicationContext(),
                    prolist, R.layout.item_famille_article, from,
                    views);
            lstpro.setAdapter(ADA);


            lstpro.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    HashMap<String, Object> obj = (HashMap<String, Object>) ADA
                            .getItem(arg2);
                    codefamille = (String) obj.get("B");


                    LayoutInflater li = LayoutInflater.from(co);
                    View px = li.inflate(R.layout.diaglistarticle, null);
                    AlertDialog.Builder alt = new AlertDialog.Builder(co);
                    alt.setIcon(R.drawable.i2s);
                    alt.setTitle("Article");
                    alt.setView(px);
                    connectionClass = new ConnectionClass();
                    //  listarticle = (ListView) px.findViewById(R.id.listaricle);
                    grid_article = (GridView) px.findViewById(R.id.grid_article);
                    FillListarticle fillListarticle = new FillListarticle();
                    fillListarticle.execute("");

                    alt.setCancelable(false)
                            .setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface di, int i) {

                                        }
                                    })
                    ;
                    AlertDialog d = alt.create();
                    d.show();


                }
            });

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN(ip, password, user, base);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    querylist = "select * from FamilleArticle";
                    PreparedStatement ps = con.prepareStatement(querylist);
                    ResultSet rs = ps.executeQuery();

                    ArrayList data1 = new ArrayList();
                    while (rs.next()) {
                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("A", rs.getString("Libelle"));
                        datanum.put("B", rs.getString("CodeFamille"));
                        image = rs.getString("logoFamille");


                        prolist.add(datanum);
                        z = "Success";
                    }


                }
            } catch (SQLException ex) {
                z = "list" + ex.toString();

            }
            return z;
        }
    }

    public class FillListarticle extends AsyncTask<String, String, String> {
        String z = "", image = "";

        List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {
            i = 0;

        }

        @Override
        protected void onPostExecute(String r) {

//
            // Toast.makeText(getApplicationContext(), r, Toast.LENGTH_SHORT).show();

            String[] from = {"Designation", "PrixVenteTTC", "Quantite", "CodeArticle", "B", "F", "G", "H", "I", "J"};
            int[] views = {R.id.codart, R.id.prixart, R.id.tqt};
            final SimpleAdapter ADA = new SimpleAdapter(getApplicationContext(),
                    prolist, R.layout.listarticle, from,
                    views);
            // listarticle.setAdapter(ADA);


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
                    convertView = layoutInflater.inflate(R.layout.listarticle, null);
                    final TextView txt_codart = (TextView) convertView.findViewById(R.id.codart);

                    final TextView txt_prixart = (TextView) convertView.findViewById(R.id.prixart);
                    final TextView txt_tqt = (TextView) convertView.findViewById(R.id.tqt);
                    final TextView txt_designationarticle = (TextView) convertView.findViewById(R.id.designationarticle);
                    final Button bt_moins = (Button) convertView.findViewById(R.id.bt_moins);
                    final Button bt_plus = (Button) convertView.findViewById(R.id.bt_plus);


                    final CardView cardView = (CardView) convertView.findViewById(R.id.btn_item);
                    final HashMap<String, Object> obj = (HashMap<String, Object>) ADA
                            .getItem(position);

                    final String CodeArticle = (String) obj.get("CodeArticle");
                    final String Quantite = (String) obj.get("Quantite");
                    String Designation = (String) obj.get("Designation");
                    String PrixVenteTTC = (String) obj.get("PrixVenteTTC");

                    txt_codart.setText(CodeArticle);
                    txt_tqt.setText(Quantite);
                    txt_prixart.setText(PrixVenteTTC);
                    txt_designationarticle.setText(Designation);

                    bt_moins.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            float ancien_qt = Float.parseFloat(txt_tqt.getText().toString());
                            float nv_qt = ancien_qt - 1;

                            CodeArticleSelection = txt_codart.getText().toString();
                            QteSaisi = nv_qt + "";

                            Log.e("infoart", CodeArticleSelection + "=" + QteSaisi);
                            if (nv_qt < 0) {


                            } else {
                                UpdateLigneTempTicketTemporelle updateLigneTempTicketTemporelle = new UpdateLigneTempTicketTemporelle();
                                updateLigneTempTicketTemporelle.execute("");
                            }
                        }
                    });

                    bt_plus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            float ancien_qt = Float.parseFloat(txt_tqt.getText().toString());
                            float nv_qt = ancien_qt + 1;

                            CodeArticleSelection = txt_codart.getText().toString();
                            QteSaisi = nv_qt + "";
                            txt_tqt.setText(QteSaisi);
                            Log.e("infoart", CodeArticleSelection + "=" + QteSaisi);
                            if (nv_qt == 1) {

                                AjoutLigneTempTicketTemporelle ajoutLigneTempTicketTemporelle = new AjoutLigneTempTicketTemporelle();
                                ajoutLigneTempTicketTemporelle.execute("");


                            } else {
                                UpdateLigneTempTicketTemporelle updateLigneTempTicketTemporelle = new UpdateLigneTempTicketTemporelle();
                                updateLigneTempTicketTemporelle.execute("");
                            }


                        }
                    });


                    return convertView;
                }
            };


            grid_article.setAdapter(baseAdapter);


            grid_article.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    HashMap<String, Object> obj = (HashMap<String, Object>) ADA
                            .getItem(arg2);
                    proid = (String) obj.get("CodeArticle");
                    CodeArticleSelection = (String) obj.get("CodeArticle");
                    QteSaisi = (String) obj.get("Quantite");
                    if (Float.parseFloat(QteSaisi) > 1) {

                    } else {

                    }


                }
            });

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN(ip, password, user, base);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

                    String q = "\n" +
                            "\n" +
                            "select Article.CodeArticle,Article.Designation,Article.PrixVenteTTC \n" +
                            " ,isnull((select isnull( Quantite,0) from TempLigneTicketTemporaire where NumeroBonLivraisonVente='" + NumeroBonLivraisonVente + "' \n" +
                            " and TempLigneTicketTemporaire.CodeArticle=Article.CodeArticle),0) as Quantite\n" +
                            "from Article where CodeFamille= '" + codefamille + "'";
                    PreparedStatement ps = con.prepareStatement(q);
                    Log.e("queryarticle", q);
                    ResultSet rs = ps.executeQuery();

                    ArrayList data1 = new ArrayList();
                    while (rs.next()) {
                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("Designation", rs.getString("Designation"));
                        datanum.put("CodeArticle", rs.getString("CodeArticle"));

                        String p = rs.getString("PrixVenteTTC");
                        datanum.put("PrixVenteTTC", p);

                        datanum.put("Quantite", rs.getString("Quantite"));


                        prolist.add(datanum);
                        z = "Success";
                    }


                }
            } catch (SQLException ex) {
                z = "list" + ex.toString();

            }
            return z;
        }
    }

    public class FillListArticleCommande extends AsyncTask<String, String, String> {
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

            String[] from = {"DesignationArticle", "Quantite", "CodeArticle", "QuantiteCommander", "F", "G", "H", "I", "J"};
            int[] views = {R.id.codart, R.id.tqt,R.id.txt_qtcmd};
            final SimpleAdapter ADA = new SimpleAdapter(getApplicationContext(),
                    prolist, R.layout.listarticleencour, from,
                    views);
            listcmd.setAdapter(ADA);


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
                    convertView = layoutInflater.inflate(R.layout.listarticleencour, null);
                    final TextView txt_codart = (TextView) convertView.findViewById(R.id.codart);

                    final TextView txt_prixart = (TextView) convertView.findViewById(R.id.prixart);
                    final TextView txt_qtcmd = (TextView) convertView.findViewById(R.id.txt_qtcmd);
                    final TextView txt_tqt = (TextView) convertView.findViewById(R.id.tqt);
                    final TextView txt_designationarticle = (TextView) convertView.findViewById(R.id.designationarticle);
                    final Button bt_moins = (Button) convertView.findViewById(R.id.bt_moins);
                    final Button bt_plus = (Button) convertView.findViewById(R.id.bt_plus);


                    final CardView cardView = (CardView) convertView.findViewById(R.id.btn_item);
                    final HashMap<String, Object> obj = (HashMap<String, Object>) ADA
                            .getItem(position);

                    final String CodeArticle = (String) obj.get("CodeArticle");
                    final String Quantite = (String) obj.get("Quantite");
                    String Designation = (String) obj.get("DesignationArticle");
                    String PrixVenteTTC = (String) obj.get("PrixVenteTTC");
                    String qtcmd = (String) obj.get("QuantiteCommander");

                    txt_codart.setText(CodeArticle);
                    txt_qtcmd.setText(qtcmd);
                    txt_tqt.setText(Quantite);
                    txt_prixart.setText(PrixVenteTTC);
                    txt_designationarticle.setText(Designation);

                    bt_moins.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            float ancien_qt = Float.parseFloat(txt_tqt.getText().toString());
                            float nv_qt = ancien_qt - 1;

                            CodeArticleSelection = txt_codart.getText().toString();
                            QteSaisi = nv_qt + "";

                            Log.e("infoart", CodeArticleSelection + "=" + QteSaisi);
                            if (nv_qt < 0) {


                            } else {
                                UpdateLigneTempTicketTemporelle updateLigneTempTicketTemporelle = new UpdateLigneTempTicketTemporelle();
                                updateLigneTempTicketTemporelle.execute("");
                            }
                        }
                    });

                    bt_plus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            float ancien_qt = Float.parseFloat(txt_tqt.getText().toString());
                            float nv_qt = ancien_qt + 1;

                            CodeArticleSelection = txt_codart.getText().toString();
                            QteSaisi = nv_qt + "";
                            txt_tqt.setText(QteSaisi);
                            Log.e("infoart", CodeArticleSelection + "=" + QteSaisi);
                            if (nv_qt == 1) {

                                AjoutLigneTempTicketTemporelle ajoutLigneTempTicketTemporelle = new AjoutLigneTempTicketTemporelle();
                                ajoutLigneTempTicketTemporelle.execute("");


                            } else {
                                UpdateLigneTempTicketTemporelle updateLigneTempTicketTemporelle = new UpdateLigneTempTicketTemporelle();
                                updateLigneTempTicketTemporelle.execute("");
                            }


                        }
                    });


                    return convertView;
                }
            };


            listcmd.setAdapter(baseAdapter);


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN(ip, password, user, base);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {


                    String queryTable = "select* , isnull((select  Quantite  from TempLigneTicket\n" +
                            "where NumeroBonLivraisonVente='"+NumeroBonLivraisonVente+"' and " +
                            "TempLigneTicket.CodeArticle=TempLigneTicketTemporaire.CodeArticle ) ,0) as QuantiteCommander  from TempLigneTicketTemporaire WHERE " +
                            "NumeroBonLivraisonVente='" + NumeroBonLivraisonVente + "'  ";

                    PreparedStatement ps = con.prepareStatement(queryTable);
                    Log.e("query", queryTable);

                    ResultSet rs = ps.executeQuery();
                    z = "e";

                    while (rs.next()) {

                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("CodeArticle", rs.getString("CodeArticle"));

                        datanum.put("DesignationArticle", rs.getString("DesignationArticle"));
                        datanum.put("Quantite", rs.getString("Quantite"));
                        datanum.put("QuantiteCommander", rs.getString("QuantiteCommander"));
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

    public class FillListArticleCommandeLancer extends AsyncTask<String, String, String> {
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

            String[] from = {"DesignationArticle", "Quantite", "CodeArticle", "B", "F", "G", "H", "I", "J"};
            int[] views = {R.id.codart, R.id.tqt};
            final SimpleAdapter ADA = new SimpleAdapter(getApplicationContext(),
                    prolist, R.layout.listarticlelancer, from,
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
                    convertView = layoutInflater.inflate(R.layout.listarticlelancer, null);
                    final TextView txt_codart = (TextView) convertView.findViewById(R.id.codart);

                    final TextView txt_prixart = (TextView) convertView.findViewById(R.id.prixart);
                    final TextView txt_tqt = (TextView) convertView.findViewById(R.id.tqt);
                    final TextView txt_designationarticle = (TextView) convertView.findViewById(R.id.designationarticle);


                    final CardView cardView = (CardView) convertView.findViewById(R.id.btn_item);
                    final HashMap<String, Object> obj = (HashMap<String, Object>) ADA
                            .getItem(position);

                    final String CodeArticle = (String) obj.get("CodeArticle");
                    final String Quantite = (String) obj.get("Quantite");
                    String Designation = (String) obj.get("DesignationArticle");
                    String PrixVenteTTC = (String) obj.get("PrixVenteTTC");

                    txt_codart.setText(CodeArticle);
                    txt_tqt.setText(Quantite);
                    txt_prixart.setText(PrixVenteTTC);
                    txt_designationarticle.setText(Designation);


                    return convertView;
                }
            };


            grid_article_lancer.setAdapter(baseAdapter);


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN(ip, password, user, base);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {


                    String queryTable = "select* from TempLigneTicket  WHERE NumeroBonLivraisonVente='" + NumeroBonLivraisonVente + "'";

                    PreparedStatement ps = con.prepareStatement(queryTable);
                    Log.e("query", queryTable);

                    ResultSet rs = ps.executeQuery();
                    z = "e";

                    while (rs.next()) {

                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("CodeArticle", rs.getString("CodeArticle"));

                        datanum.put("DesignationArticle", rs.getString("DesignationArticle"));
                        datanum.put("Quantite", rs.getString("Quantite"));
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


    public class AjoutLigneTempTicketTemporelle extends AsyncTask<String, String, String> {
        String z = "", image = "";

        String query = "";

        @Override
        protected void onPreExecute() {


            if (NumeroBonLivraisonVente.equals("")) {

                try {
                    Connection con = connectionClass.CONN(ip, password, user, base);
                    if (con == null) {
                        z = "Error in connection with SQL server";
                    } else {

                        String query = "Select * from CompteurPiece  where NomPiecer='Ticket'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()) {
                            String anciencompteur = rs.getString("Compteur");

                            String annee = rs.getString("Annee");
                            String PrefixPiece = rs.getString("PrefixPiece");
                            String pre = PrefixPiece + annee + anciencompteur;
                            NumeroBonLivraisonVente = pre;
                            Float comp = Float.parseFloat(anciencompteur);
                            comp++;

                            DecimalFormat numberFormat = new DecimalFormat("00000");
                            String st = numberFormat.format(comp);


                            String query2 = "update CompteurPiece  set Compteur='" + st + "' where NomPiecer='Ticket'";
                            PreparedStatement preparedStatement = con.prepareStatement(query2);


                            preparedStatement.executeUpdate();


                        }
                        textnum_ticket.setText(NumeroBonLivraisonVente);
                        String query_inst = " INSERT INTO  TempTicketTemporaire  \n" +
                                "           ( NumeroBonLivraisonVente \n" +
                                "           , DateBonLivraisonVente \n" +
                                "           , NumeroBonCommandeVente \n" +
                                "           , CodeClient \n" +
                                "           , TotalHT \n" +
                                "           , TotalRemise \n" +
                                "           , TotalFodec \n" +
                                "           , TotalNetHT \n" +
                                "           , TotalTVA \n" +
                                "           , TotalTTC \n" +
                                "           , NumeroEtat \n" +
                                "           , NumeroFactureVente \n" +
                                "           , NomUtilisateur \n" +
                                "           , DateCreation \n" +
                                "           , HeureCreation \n" +
                                "           , Observation \n" +
                                "           , ReferenceBonCommande \n" +
                                "           , MontantRecu \n" +
                                "           , MontantRendu \n" +
                                "           , NumeroTable \n" +
                                "           , NbCouverts \n" +
                                "           , TauxRemise \n" +
                                "           , ImprimAddition \n" +
                                "           , CodeDepot )\n" +
                                "     VALUES\n" +
                                "           ('" + NumeroBonLivraisonVente + "'\n" +
                                "           ,GETDATE()\n" +
                                "           ,''\n" +
                                "           , '418'\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,'E05'\n" +
                                "           , ''\n" +
                                "           ,'" + NomUtilisateur + "'\n" +
                                "           ,GETDATE()\n" +
                                "           ,GETDATE()\n" +
                                "           ,''\n" +
                                "           ,''\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,'" + NumeroTable + "'\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,'DEP002')\n" +
                                " ";
                        PreparedStatement preparedStatement = con.prepareStatement(query_inst);


                        preparedStatement.executeUpdate();


                        String query2 = " INSERT INTO  TempTicket \n" +
                                "           ( NumeroBonLivraisonVente \n" +
                                "           , DateBonLivraisonVente \n" +
                                "           , NumeroBonCommandeVente \n" +
                                "           , CodeClient \n" +
                                "           , TotalHT \n" +
                                "           , TotalRemise \n" +
                                "           , TotalFodec \n" +
                                "           , TotalNetHT \n" +
                                "           , TotalTVA \n" +
                                "           , TotalTTC \n" +
                                "           , NumeroEtat \n" +
                                "           , NumeroFactureVente \n" +
                                "           , NomUtilisateur \n" +
                                "           , DateCreation \n" +
                                "           , HeureCreation \n" +
                                "           , Observation \n" +
                                "           , ReferenceBonCommande \n" +
                                "           , MontantRecu \n" +
                                "           , MontantRendu \n" +
                                "           , NumeroTable \n" +
                                "           , NbCouverts \n" +
                                "           , TauxRemise \n" +
                                "           , ImprimAddition \n" +
                                "           , CodeDepot )\n" +
                                "     VALUES\n" +
                                "           ('" + NumeroBonLivraisonVente + "'\n" +
                                "           ,GETDATE()\n" +
                                "           ,''\n" +
                                "           , '418'\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,'E05'\n" +
                                "           , ''\n" +
                                "           ,'" + NomUtilisateur + "'\n" +
                                "           ,GETDATE()\n" +
                                "           ,GETDATE()\n" +
                                "           ,''\n" +
                                "           ,''\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,'" + NumeroTable + "'\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,'DEP002')\n" +
                                " ";
                        PreparedStatement preparedStatement2 = con.prepareStatement(query2);


                        preparedStatement2.executeUpdate();


                    }
                } catch (SQLException ex) {
                    z = "echec import ancien compteur" + ex.toString();
                }

            }


            query = " INSERT INTO  TempLigneTicketTemporaire \n" +
                    "           ( NumeroBonLivraisonVente \n" +
                    "           , CodeArticle \n" +
                    "           , DesignationArticle \n" +
                    "           , NumeroOrdre \n" +
                    "           , PrixVenteHT \n" +
                    "           , Quantite \n" +
                    "           , MontantHT \n" +
                    "           , TauxRemise \n" +
                    "           , MontantRemise \n" +
                    "           , MontantFodec \n" +
                    "           , NetHT \n" +
                    "           , TauxTVA \n" +
                    "           , MontantTVA \n" +
                    "           , MontantTTC \n" +
                    "           , Observation \n" +
                    "           , PrixAchatNet \n" +
                    "           , Imprimer \n" +
                    "           , CodeDepot \n" +
                    "           , QteImprimer )\n" +
                    "     VALUES\n" +
                    "           ( '" + NumeroBonLivraisonVente + "'" +
                    "           , '" + CodeArticleSelection + "' " +
                    "           , (select Designation from Article where CodeArticle='" + CodeArticleSelection + "')" +
                    "           , (select  ISNULL( MAX(NumeroOrdre)+1,1) from TempLigneTicketTemporaire where NumeroBonLivraisonVente='" + NumeroBonLivraisonVente + "')" +
                    "           , (select isnull(PrixVenteHT ,0) from Article where CodeArticle='" + CodeArticleSelection + "') \n" +
                    "           ," + QteSaisi +
                    "           , (select isnull(PrixVenteHT* " + QteSaisi + ",0) from Article where CodeArticle='" + CodeArticleSelection + "') \n" +
                    "           ,0" +
                    "           ,0" +
                    "           ,0" +
                    "           , (select isnull(PrixVenteHT* " + QteSaisi + ",0) from Article where CodeArticle='" + CodeArticleSelection + "') \n" +
                    "           , (select  TVA.TauxTVA   from Article inner join TVA ON TVA.CodeTVA=Article.CodeTVA  where CodeArticle='" + CodeArticleSelection + "')" +
                    "           , (select  TVA.TauxTVA *PrixVenteHT/100* " + QteSaisi + " from Article inner join TVA ON TVA.CodeTVA=Article.CodeTVA  where CodeArticle='" + CodeArticleSelection + "')" +
                    "           ,( select  (TVA.TauxTVA /100+1)*PrixVenteHT* " + QteSaisi + " from Article inner join TVA ON TVA.CodeTVA=Article.CodeTVA  where CodeArticle='" + CodeArticleSelection + "')" +

                    "           , ''" +
                    "           , (select PrixAchatHT from Article where CodeArticle='" + CodeArticleSelection + "')" +
                    "           ,0" +
                    "           , '" + CodeDepot + "'" +
                    "           ,0)";
            Log.e("ajoutlignetemporelle", query);

        }

        @Override
        protected void onPostExecute(String r) {
            Toast.makeText(getApplicationContext(), "ajout article" + r, Toast.LENGTH_LONG).show();

            FillListArticleCommande fillListArticleCommande = new FillListArticleCommande();
            fillListArticleCommande.execute("");
            UpdateTempTicketTemporelle updateTempTicketTemporelle = new UpdateTempTicketTemporelle();
            updateTempTicketTemporelle.execute("");


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN(ip, password, user, base);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {


                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.executeUpdate();


                }
            } catch (SQLException ex) {
                z = "list" + ex.toString();
                Log.e("erreurlignetemporelle", z);
            }
            return z;
        }
    }


    public class UpdateLigneTempTicketTemporelle extends AsyncTask<String, String, String> {
        String z = "", image = "";
        String query = "";


        @Override
        protected void onPreExecute() {
            query = " update   TempLigneTicketTemporaire \n" +

                    "           set Quantite=" + QteSaisi +
                    "           ,  MontantHT =(select isnull(PrixVenteHT* " + QteSaisi + ",0) from Article where CodeArticle='" + CodeArticleSelection + "') \n" +
                    "           , NetHT=(select isnull(PrixVenteHT* " + QteSaisi + ",0) from Article where CodeArticle='" + CodeArticleSelection + "') \n" +
                    "           ,MontantTVA= (select  TVA.TauxTVA *PrixVenteHT/100* " + QteSaisi + " from Article inner join TVA ON TVA.CodeTVA=Article.CodeTVA  where CodeArticle='" + CodeArticleSelection + "')" +
                    "           , MontantTTC=(select  (TVA.TauxTVA /100+1)*PrixVenteHT* " + QteSaisi + " from Article inner join TVA ON TVA.CodeTVA=Article.CodeTVA  where CodeArticle='" + CodeArticleSelection + "')" +
                    "              where NumeroBonLivraisonVente= '" + NumeroBonLivraisonVente + "'" +
                    "           and CodeArticle= '" + CodeArticleSelection + "' ";
            Log.e("modiflignetemporelle", query);
        }

        @Override
        protected void onPostExecute(String r) {
            Toast.makeText(getApplicationContext(), "ajout article" + r, Toast.LENGTH_LONG).show();

            FillListArticleCommande fillListArticleCommande = new FillListArticleCommande();
            fillListArticleCommande.execute("");

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN(ip, password, user, base);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {


                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.executeUpdate();


                }
            } catch (SQLException ex) {
                z = ex.toString();
                Log.e("modiflignetemporelle", z);
            }
            return z;
        }
    }


    public class UpdateTempTicketTemporelle extends AsyncTask<String, String, String> {
        String z = "", image = "";
        String query = "";


        @Override
        protected void onPreExecute() {


            query = " update TempTicketTemporaire \n" +
                    " set \n" +
                    " TotalHT=(select isnull(sum(MontantHT),0) from TempLigneTicketTemporaire where  NumeroBonLivraisonVente='" + NumeroBonLivraisonVente + "'   )\n" +
                    ", TotalNetHT=(select isnull(sum(NetHT),0) from TempLigneTicketTemporaire where  NumeroBonLivraisonVente='" + NumeroBonLivraisonVente + "' )\n" +
                    ", TotalTVA=(select isnull(sum(MontantTVA),0) from TempLigneTicketTemporaire where  NumeroBonLivraisonVente='" + NumeroBonLivraisonVente + "' )\n" +
                    ", TotalTTC=(select isnull(sum(MontantTTC),0) from TempLigneTicketTemporaire  where  NumeroBonLivraisonVente='" + NumeroBonLivraisonVente + "' )\n" +
                    " where  NumeroBonLivraisonVente='" + NumeroBonLivraisonVente + "' " +
                    " \n " +
                    " update TempTicket  \n" +
                    " set \n" +
                    " TotalHT=(select isnull(sum(MontantHT),0) from TempLigneTicket  where  NumeroBonLivraisonVente='" + NumeroBonLivraisonVente + "'   )\n" +
                    ", TotalNetHT=(select isnull(sum(NetHT),0) from TempLigneTicket  where  NumeroBonLivraisonVente='" + NumeroBonLivraisonVente + "' )\n" +
                    ", TotalTVA=(select isnull(sum(MontantTVA),0) from TempLigneTicket  where  NumeroBonLivraisonVente='" + NumeroBonLivraisonVente + "' )\n" +
                    ", TotalTTC=(select isnull(sum(MontantTTC),0) from TempLigneTicket   where  NumeroBonLivraisonVente='" + NumeroBonLivraisonVente + "' )\n" +
                    " where  NumeroBonLivraisonVente='" + NumeroBonLivraisonVente + "'";

            Log.e("update total", query);
        }

        @Override
        protected void onPostExecute(String r) {
            Toast.makeText(getApplicationContext(), "update total" + r, Toast.LENGTH_LONG).show();

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN(ip, password, user, base);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {


                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.executeUpdate();


                }
            } catch (SQLException ex) {
                z = ex.toString();
                Log.e("totaltemporelle", z);
            }
            return z;
        }
    }


    public class LancerCommande extends AsyncTask<String, String, String> {
        String z = "", image = "";

        String query = "";

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onPostExecute(String r) {
            Toast.makeText(getApplicationContext(), "lancer commande" + r, Toast.LENGTH_LONG).show();
            UpdateTempTicketTemporelle updateTempTicketTemporelle = new UpdateTempTicketTemporelle();
            updateTempTicketTemporelle.execute("");

            Intent intent = new Intent(getApplicationContext(), ChoixTable.class);
            startActivity(intent);

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN(ip, password, user, base);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {


                    String queryTable = "select TempLigneTicketTemporaire.CodeArticle ,TempLigneTicketTemporaire.Quantite as qtAjout,\n" +
                            "isnull(TempLigneTicket.Quantite,0) as qtAncien ,\n" +
                            "(TempLigneTicketTemporaire.Quantite +isnull(TempLigneTicket.Quantite,0) )as qtNouveau\n" +
                            ", \n" +
                            "case  \n" +
                            "when TempLigneTicket.CodeArticle is null then 0\n" +
                            "else 1\n" +
                            "end  as Existe \n" +
                            "from TempLigneTicketTemporaire \n" +
                            "left join TempLigneTicket\n" +
                            "on TempLigneTicketTemporaire.NumeroBonLivraisonVente=TempLigneTicket.NumeroBonLivraisonVente\n" +
                            " and TempLigneTicketTemporaire.CodeArticle=TempLigneTicket.CodeArticle\n" +
                            "where TempLigneTicketTemporaire.NumeroBonLivraisonVente='" + NumeroBonLivraisonVente + "' \n" +
                            " ";

                    PreparedStatement ps = con.prepareStatement(queryTable);
                    Log.e("query", queryTable);

                    ResultSet rs = ps.executeQuery();
                    z = "e";

                    while (rs.next()) {

                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("CodeArticle", rs.getString("CodeArticle"));

                        datanum.put("qtAjout", rs.getString("qtAjout"));
                        datanum.put("qtAncien", rs.getString("qtAncien"));
                        datanum.put("qtNouveau", rs.getString("qtNouveau"));
                        datanum.put("Existe", rs.getString("Existe"));
                        String Existe = rs.getString("Existe");
                        String CodeArticleSelection = rs.getString("CodeArticle");
                        String QteSaisi = rs.getString("qtAjout");
                        if (Existe.equals("0")) {
                            query = " INSERT INTO  TempLigneTicket \n" +
                                    "           ( NumeroBonLivraisonVente \n" +
                                    "           , CodeArticle \n" +
                                    "           , DesignationArticle \n" +
                                    "           , NumeroOrdre \n" +
                                    "           , PrixVenteHT \n" +
                                    "           , Quantite \n" +
                                    "           , MontantHT \n" +
                                    "           , TauxRemise \n" +
                                    "           , MontantRemise \n" +
                                    "           , MontantFodec \n" +
                                    "           , NetHT \n" +
                                    "           , TauxTVA \n" +
                                    "           , MontantTVA \n" +
                                    "           , MontantTTC \n" +
                                    "           , Observation \n" +
                                    "           , PrixAchatNet \n" +
                                    "           , Imprimer \n" +
                                    "           , CodeDepot \n" +
                                    "           , QteImprimer )\n" +
                                    "     VALUES\n" +
                                    "           ( '" + NumeroBonLivraisonVente + "'" +
                                    "           , '" + CodeArticleSelection + "' " +
                                    "           , (select Designation from Article where CodeArticle='" + CodeArticleSelection + "')" +
                                    "           , (select  ISNULL( MAX(NumeroOrdre),1) from TempLigneTicketTemporaire where NumeroBonLivraisonVente='" + NumeroBonLivraisonVente + "')" +
                                    "           , (select isnull(PrixVenteHT ,0) from Article where CodeArticle='" + CodeArticleSelection + "') \n" +
                                    "           ," + QteSaisi +
                                    "           , (select isnull(PrixVenteHT* " + QteSaisi + ",0) from Article where CodeArticle='" + CodeArticleSelection + "') \n" +
                                    "           ,0" +
                                    "           ,0" +
                                    "           ,0" +
                                    "           , (select isnull(PrixVenteHT* " + QteSaisi + ",0) from Article where CodeArticle='" + CodeArticleSelection + "') \n" +
                                    "           , (select  TVA.TauxTVA   from Article inner join TVA ON TVA.CodeTVA=Article.CodeTVA  where CodeArticle='" + CodeArticleSelection + "')" +
                                    "           , (select  TVA.TauxTVA *PrixVenteHT/100* " + QteSaisi + " from Article inner join TVA ON TVA.CodeTVA=Article.CodeTVA  where CodeArticle='" + CodeArticleSelection + "')" +
                                    "           ,( select  (TVA.TauxTVA /100+1)*PrixVenteHT* " + QteSaisi + " from Article inner join TVA ON TVA.CodeTVA=Article.CodeTVA  where CodeArticle='" + CodeArticleSelection + "')" +

                                    "           , ''" +
                                    "           , (select PrixAchatHT from Article where CodeArticle='" + CodeArticleSelection + "')" +
                                    "           ,0" +
                                    "           , '" + CodeDepot + "'" +
                                    "           ,0)";
                        } else {

                            query = " update   TempLigneTicket \n" +

                                    "           set Quantite=" + QteSaisi +
                                    "           ,  MontantHT =(select isnull(PrixVenteHT* " + QteSaisi + ",0) from Article where CodeArticle='" + CodeArticleSelection + "') \n" +
                                    "           , NetHT=(select isnull(PrixVenteHT* " + QteSaisi + ",0) from Article where CodeArticle='" + CodeArticleSelection + "') \n" +
                                    "           ,MontantTVA= (select  TVA.TauxTVA *PrixVenteHT/100* " + QteSaisi + " from Article inner join TVA ON TVA.CodeTVA=Article.CodeTVA  where CodeArticle='" + CodeArticleSelection + "')" +
                                    "           , MontantTTC=(select  (TVA.TauxTVA /100+1)*PrixVenteHT* " + QteSaisi + " from Article inner join TVA ON TVA.CodeTVA=Article.CodeTVA  where CodeArticle='" + CodeArticleSelection + "')" +
                                    "           ,Imprimer=0    " +
                                    "           where NumeroBonLivraisonVente= '" + NumeroBonLivraisonVente + "'" +
                                    "           and CodeArticle= '" + CodeArticleSelection + "' ";
                            Log.e("valide update artu", query);


                        }

                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        preparedStatement.executeUpdate();


                        z = "succees";
                    }

                }
            } catch (SQLException ex) {
                z = "list" + ex.toString();
                Log.e("erreurlignetemporelle", z);
            }
            return z;
        }
    }

    public class Compteur extends AsyncTask<String, String, String> {


        Boolean isSuccess = false;
        String z = "";
        String c, pre = "";

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String r) {
            Toast.makeText(getApplicationContext(), r + NumeroBonLivraisonVente, Toast.LENGTH_SHORT).show();
            textnum_ticket.setText(NumeroBonLivraisonVente);
        }

        @Override
        protected String doInBackground(String... params) {
            if (false) {
                z = "Erreur ";
            } else {
                try {
                    Connection con = connectionClass.CONN(ip, password, user, base);
                    if (con == null) {
                        z = "Error in connection with SQL server";
                    } else {


                        String query = "Select * from CompteurPiece  where NomPiecer='Ticket'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()) {
                            String anciencompteur = rs.getString("Compteur");

                            String annee = rs.getString("Annee");
                            String PrefixPiece = rs.getString("PrefixPiece");
                            pre = PrefixPiece + annee + anciencompteur;
                            NumeroBonLivraisonVente = pre;
                            Float comp = Float.parseFloat(anciencompteur);
                            comp++;

                            DecimalFormat numberFormat = new DecimalFormat("00000");
                            String st = numberFormat.format(comp);


                            String query2 = "update CompteurPiece  set Compteur='" + st + "' where NomPiecer='Ticket'";
                            PreparedStatement preparedStatement = con.prepareStatement(query2);


                            preparedStatement.executeUpdate();

                            isSuccess = true;


                        }


                    }
                } catch (SQLException ex) {
                    isSuccess = false;
                    z = "echec import ancien compteur" + ex.toString();
                }
            }
            return z;
        }
    }

    public class OuvertureTicket extends AsyncTask<String, String, String> {


        Boolean isSuccess = false;
        String z = "";
        String c, pre = "";

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onPostExecute(String r) {
            // Toast.makeText(getApplicationContext(), r + NumCMD, Toast.LENGTH_SHORT).show();
            textnum_ticket.setText(NumeroBonLivraisonVente);


        }

        @Override
        protected String doInBackground(String... params) {
            if (false) {
                z = "Erreur ";
            } else {
                try {
                    Connection con = connectionClass.CONN(ip, password, user, base);
                    if (con == null) {
                        z = "Error in connection with SQL server";
                    } else {

                        String query = "Select * from CompteurPiece  where NomPiecer='Ticket'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()) {
                            String anciencompteur = rs.getString("Compteur");

                            String annee = rs.getString("Annee");
                            String PrefixPiece = rs.getString("PrefixPiece");
                            pre = PrefixPiece + annee + anciencompteur;
                            NumeroBonLivraisonVente = pre;
                            Float comp = Float.parseFloat(anciencompteur);
                            comp++;

                            DecimalFormat numberFormat = new DecimalFormat("00000");
                            String st = numberFormat.format(comp);


                            String query2 = "update CompteurPiece  set Compteur='" + st + "' where NomPiecer='Ticket'";
                            PreparedStatement preparedStatement = con.prepareStatement(query2);


                            preparedStatement.executeUpdate();

                            isSuccess = true;


                        }

                        String query_inst = " INSERT INTO  TempTicketTemporaire  \n" +
                                "           ( NumeroBonLivraisonVente \n" +
                                "           , DateBonLivraisonVente \n" +
                                "           , NumeroBonCommandeVente \n" +
                                "           , CodeClient \n" +
                                "           , TotalHT \n" +
                                "           , TotalRemise \n" +
                                "           , TotalFodec \n" +
                                "           , TotalNetHT \n" +
                                "           , TotalTVA \n" +
                                "           , TotalTTC \n" +
                                "           , NumeroEtat \n" +
                                "           , NumeroFactureVente \n" +
                                "           , NomUtilisateur \n" +
                                "           , DateCreation \n" +
                                "           , HeureCreation \n" +
                                "           , Observation \n" +
                                "           , ReferenceBonCommande \n" +
                                "           , MontantRecu \n" +
                                "           , MontantRendu \n" +
                                "           , NumeroTable \n" +
                                "           , NbCouverts \n" +
                                "           , TauxRemise \n" +
                                "           , ImprimAddition \n" +
                                "           , CodeDepot )\n" +
                                "     VALUES\n" +
                                "           ('" + NumeroBonLivraisonVente + "'\n" +
                                "           ,GETDATE()\n" +
                                "           ,''\n" +
                                "           , '418'\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,'E05'\n" +
                                "           , ''\n" +
                                "           ,'" + NomUtilisateur + "'\n" +
                                "           ,GETDATE()\n" +
                                "           ,GETDATE()\n" +
                                "           ,''\n" +
                                "           ,''\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,'" + NumeroTable + "'\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,'DEP002')\n" +
                                " ";
                        PreparedStatement preparedStatement = con.prepareStatement(query_inst);


                        preparedStatement.executeUpdate();


                        String query2 = " INSERT INTO  TempTicket \n" +
                                "           ( NumeroBonLivraisonVente \n" +
                                "           , DateBonLivraisonVente \n" +
                                "           , NumeroBonCommandeVente \n" +
                                "           , CodeClient \n" +
                                "           , TotalHT \n" +
                                "           , TotalRemise \n" +
                                "           , TotalFodec \n" +
                                "           , TotalNetHT \n" +
                                "           , TotalTVA \n" +
                                "           , TotalTTC \n" +
                                "           , NumeroEtat \n" +
                                "           , NumeroFactureVente \n" +
                                "           , NomUtilisateur \n" +
                                "           , DateCreation \n" +
                                "           , HeureCreation \n" +
                                "           , Observation \n" +
                                "           , ReferenceBonCommande \n" +
                                "           , MontantRecu \n" +
                                "           , MontantRendu \n" +
                                "           , NumeroTable \n" +
                                "           , NbCouverts \n" +
                                "           , TauxRemise \n" +
                                "           , ImprimAddition \n" +
                                "           , CodeDepot )\n" +
                                "     VALUES\n" +
                                "           ('" + NumeroBonLivraisonVente + "'\n" +
                                "           ,GETDATE()\n" +
                                "           ,''\n" +
                                "           , '418'\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,'E05'\n" +
                                "           , ''\n" +
                                "           ,'" + NomUtilisateur + "'\n" +
                                "           ,GETDATE()\n" +
                                "           ,GETDATE()\n" +
                                "           ,''\n" +
                                "           ,''\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,'" + NumeroTable + "'\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,0\n" +
                                "           ,'DEP002')\n" +
                                " ";
                        PreparedStatement preparedStatement2 = con.prepareStatement(query2);


                        preparedStatement2.executeUpdate();


                    }
                } catch (SQLException ex) {
                    isSuccess = false;
                    z = "echec import ancien compteur" + ex.toString();
                }
            }
            return z;
        }
    }

}
