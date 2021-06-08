package com.bousaid.quefaireaparis;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

public class GenerateURL {
    private StringBuilder base_url;
    private StringBuilder full_url;


    public GenerateURL(StringBuilder base_url, StringBuilder full_url) {
        this.base_url = base_url;
        this.full_url = full_url;
    }


    @Override
    public int hashCode() {
        return Objects.hash(base_url, full_url);
    }

    public GenerateURL() {
    }

    public StringBuilder getBase_url() {
        return base_url;
    }

    public void setBase_url(StringBuilder base_url) {
        this.base_url = base_url;
    }

    public StringBuilder getFull_url() {
        return full_url;
    }

    public void setFull_url(StringBuilder full_url) {
        this.full_url = full_url;
    }

    //Méthode permettant de modifier l'url en fonction du choix du prix
    public void completeURL_forPriceChoice(String facet, String value){

        StringBuilder addtoURL=new StringBuilder();
        StringBuilder facetURL=new StringBuilder();
        facetURL.append("&refine.");
        facetURL.append(facet);

        addtoURL.append(facetURL);
        addtoURL.append("=");
        addtoURL.append(value);

        int start_index=full_url.indexOf(String.valueOf(facetURL));
        int end_index=full_url.indexOf("&",start_index);

        if(start_index==-1){
            Log.d("FACET N'EST PAS DANS L'URL (le prix n'a pas encore été selectionné)", "");
            full_url.append(addtoURL);
        }
        else {
            //On modifie le type du prix
            Log.d("FACET EST DANS URL (le prix a déjà été selectionné)", "");
            //Si la facet est à la fin de l'url :
            if(end_index==start_index){
                full_url.replace(start_index,full_url.length(), String.valueOf(addtoURL));
            }
            //Sinon :
            else {
                full_url.replace(start_index,end_index,String.valueOf(addtoURL));
            }
        }
    }

    //Méthode permettant de modifier l'url en fonction des catégories choisies
    public void completeURL_forCategoryChoice(String category, String sous_category) throws UnsupportedEncodingException {
        int start_index;
        int end_index;
        StringBuilder addtoURL=new StringBuilder();
        addtoURL.append("category+%3A+%22");
        addtoURL.append(URLEncoder.encode(category, "UTF-8"));
        addtoURL.append("+-%3E+");
        addtoURL.append(URLEncoder.encode(sous_category, "UTF-8"));
        addtoURL.append("%22");

        String escapeHtml = URLEncoder.encode(String.valueOf(addtoURL), "UTF-8");
        System.out.println(addtoURL);

        //On cherche si la sous-catégorie a déjà été selectionnée
        start_index=full_url.indexOf(String.valueOf(addtoURL));
        end_index=start_index+addtoURL.length()-1;
        System.out.println("START :" + start_index + "END" + end_index);
        System.out.println(end_index);
        int index_refineprice = full_url.indexOf("&refine.price_type");
        int index_location=full_url.indexOf("&geofilter.distance");
        int insert_index=full_url.length();

        //Si elle n'est pas selectionnée, on l'ajoute
        if(start_index==-1){
            Log.d("ADDING TO URL", "COMPLETE_URL");
            //Si la géolocalisation est activée, on ajoute la catégorie avant (dans l'url)
            if (index_location!=-1){
                insert_index=index_location;
            }
            //Si le prix a déjà été choisi, on ajoute la catégorie avant (dans l'url)
            if (index_refineprice!=-1){
                insert_index=index_refineprice;
            }
            //Si il n'y a pas encore de choix de catégorie
            if (full_url.indexOf("category")==-1){
                full_url.insert(insert_index,addtoURL);
            }
            //Si il y a déjà un choix de catégorie
            else {
                addtoURL.insert(0,"+OR+");
                full_url.insert(insert_index,addtoURL);
            }
        }

        //Sinon, on l'a supprime
        else {
            Log.d("DELETING FROM URL", "COMPLETE_URL");
            //Si il y a un autre choix de catégorie après (dans l'url)
            if (full_url.indexOf("+OR+",end_index+1)==end_index+1) {
                System.out.println("IL Y A UN OR APRES");
                end_index=end_index+4;
            }
            //Si la catégorie/sous-catégorie est à la fin de l'url :
            System.out.println(end_index +"end , start"+ start_index);
            if(end_index==full_url.length()-1){
                System.out.println("START INDEX : "+start_index);
                //Si il y a un autre choix de catégorie avant (dans l'url)
                if (full_url.indexOf("+OR+",start_index-4)==start_index-4) {
                    System.out.println("IL Y A UN OR AVANT (FIN DE L'URL)");
                    start_index=start_index-4;
                }
                full_url.delete(start_index,full_url.length());
            }
            //Sinon :
            else {
                full_url.delete(start_index,end_index+1);
            }
        }

    }

    //Méthode permettant d'ajouter les tags dans l'url
    public void completeURL_forTags(String tags) throws UnsupportedEncodingException {
        StringBuilder addtoURL=new StringBuilder();
        addtoURL.append("tags+%3A+%22");
        String[] splitTags = tags.split(" ");
        int index=full_url.length()-1;
        //On ajoute chaque tag à l'url
        for (int i=0;i<=splitTags.length-2;i++){
            addtoURL.append(URLEncoder.encode(splitTags[i], "UTF-8"));
            addtoURL.append("%22+OR+%22");
        }
        addtoURL.append(URLEncoder.encode(splitTags[splitTags.length-1], "UTF-8"));
        addtoURL.append("%22+");
        //Si la localisation a déjà été sélectionnée, on ajoute le tag avant
        if (full_url.indexOf("&geofilter.distance")!=-1){
            index=full_url.indexOf("&geofilter.distance");
        }
        //Sinon, on ajoute le tag à la fin de l'url
        full_url.insert(index,addtoURL);
    }

      //Méthode permettant de retirer les tags dans l'url
    public void clearTags(String tags) throws UnsupportedEncodingException {
        StringBuilder deletefromURL=new StringBuilder();
        deletefromURL.append("tags+%3A+%22");
        String[] splitTags = tags.split(" ");
        for (int i=0;i<splitTags.length-2;i++){
            deletefromURL.append(URLEncoder.encode(splitTags[i], "UTF-8"));
            deletefromURL.append("%22+OR+%22");
        }
        deletefromURL.append(URLEncoder.encode(splitTags[splitTags.length-1], "UTF-8"));
        int start_index = full_url.indexOf(String.valueOf(deletefromURL));
        int end_index=start_index+deletefromURL.length();
        if (start_index!=-1)
            full_url.delete(start_index,end_index);
    }

    public void completeURL_forLocation(double lat, double lon){

        StringBuilder addtoURL=new StringBuilder();
        StringBuilder facetURL=new StringBuilder();
        facetURL.append("&geofilter.distance=");

        addtoURL.append(facetURL);
        addtoURL.append(lat);
        addtoURL.append(",");
        addtoURL.append(lon);
        addtoURL.append(",10000");

        int start_index=full_url.indexOf(String.valueOf(facetURL));
        int end_index=full_url.indexOf("&",start_index);

        if(start_index==-1){
            Log.d("FACET N'EST PAS DANS L'URL (la localisation n'a pas encore été selectionnée)", "");
            full_url.append(addtoURL);
        }
        else {
            //On modifie le type du prix
            Log.d("FACET EST DANS URL (la localisation a déjà été selectionnée)", "");
            //Si la facet est à la fin de l'url :
            if(end_index==start_index){
                full_url.replace(start_index,full_url.length(), String.valueOf(addtoURL));
            }
            //Sinon :
            else {
                full_url.replace(start_index,end_index,String.valueOf(addtoURL));
            }
        }
    }

}
