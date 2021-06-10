# Que Faire A Paris ? 


![VideoEditor_20210305_032210_Trim4](https://user-images.githubusercontent.com/77643801/121487778-b9a75b00-c9d2-11eb-8d70-5fe229c9bddc.gif)
![VideoEditor_20210305_032210_Trim5](https://user-images.githubusercontent.com/77643801/121487672-9a103280-c9d2-11eb-9231-d487db5a8c4b.gif)
![VideoEditor_20210305_032210_Trim2](https://user-images.githubusercontent.com/77643801/121487866-ce83ee80-c9d2-11eb-8bcc-4e05b4ad9810.gif)
![VideoEditor_20210305_032210_Trim3](https://user-images.githubusercontent.com/77643801/121487897-d6dc2980-c9d2-11eb-9d02-1d3f1c760f71.gif)


La mairie de Paris met à disposition une base «Que Faire à Paris ?» en accès libre Open Data. 
L’objectif de ce projet de fin d'étude est de créer une application mobile, permettant à un utilisateur d’interroger cette base en fonction de sa localisation, de ses goûts et de ses envies.
Les utilisateurs potentiels sont les franciliens ainsi que les touristes. Application développée sur Android Studio en Java.

## Interface

Voici l'interface de notre application en détail :

➢ **La page d’accueil :** elle contient les activités du moment, qui changent tous les jours.

![Capture d’écran 2021-03-16 134648](https://user-images.githubusercontent.com/77643801/121334820-4e01b700-c91a-11eb-988d-8eef8a99f40f.png)

➢ **La page de recherche :**

  ➔ Ici nous effectuons une recherche avec un tag, dont les résultats sont affichés sous forme de liste.
  
  ![Capture d’écran 2021-03-16 135056](https://user-images.githubusercontent.com/77643801/121334847-55c15b80-c91a-11eb-8271-fc2788bb079e.png)

  ➔ Ici nous avons les éléments du bouton “filtrer”, avec la catégorie “Festival/Cycle” sélectionnée.
  
  ![Capture d’écran 2021-03-16 135131](https://user-images.githubusercontent.com/77643801/121334873-5bb73c80-c91a-11eb-8c73-398af8d06300.png)
  
  ➔ Lorsque l’on appuie sur le bouton “Date” ce DatePickerDialog apparaît, qui permet de choisir une date :
  
  ![Capture d’écran 2021-03-16 135301](https://user-images.githubusercontent.com/77643801/121334904-6245b400-c91a-11eb-8eaa-86da0c2f0bca.png)
  
  ➔ Lorsque l’on appuie sur le bouton “Prix” cet AlertDialog pour le choix du prix apparaît :
  
  ![Capture d’écran 2021-03-16 135327](https://user-images.githubusercontent.com/77643801/121334928-68d42b80-c91a-11eb-9db0-63c5df9303e0.png)
  
  ➔ Si nous voulons afficher les résultats sous forme de carte, voici l’affichage :
  
  ![Capture d’écran 2021-03-16 135405](https://user-images.githubusercontent.com/77643801/121334946-6eca0c80-c91a-11eb-9cf9-c29efd334845.png)
  
  ➔ Lorsque l’on appuie sur le Snippet d’une activité sur la carte, on obtient la fenêtre d'information suivante :
  
  ![Capture d’écran 2021-03-16 135439](https://user-images.githubusercontent.com/77643801/121334970-75f11a80-c91a-11eb-8005-20cc9a7c00b1.png)
  
 ➢ **La page des favoris :**
 
 ![Capture d’écran 2021-03-16 142126](https://user-images.githubusercontent.com/77643801/121335093-94571600-c91a-11eb-8fa9-dc5c73397f7a.png)

 ➢ **La page à propos :**
 
 ![Capture d’écran 2021-03-16 135458](https://user-images.githubusercontent.com/77643801/121335147-a042d800-c91a-11eb-82e9-baac384345da.png)
 
## Spécifications fonctionnelles

L’utilisateur a la possibilité de rechercher des mots clés, de sélectionner dans un menu déroulant des catégories, une date (actuelle ou à définir) et le prix (gratuit ou payant).
Les catégories et leurs sous-catégories étants :

![Capture d’écran 2021-06-09 114926](https://user-images.githubusercontent.com/77643801/121333150-d4b59480-c918-11eb-9f9c-c91a30126fc0.png)

L’application doit retourner le résultat soit sous la forme d’une map avec des icônes, soit dans une liste.\
Lorsque l’utilisateur choisit l’affichage des résultats sous forme de liste, il y trouve des petits blocs contenant la photo, le titre et un résumé de l'évènement.\
Sous la forme de map, il trouvera le bloc correspondant lorsqu’il sélectionne une icône correspondant à un évènement.\
Bien-sûr, les moteurs de recherche sont encore disponibles que les résultats soient sous forme de liste ou de map.\
Si il y a modification dans les moteurs de recherche, alors la liste ou la map est actualisée.\
De plus, un bouton “favoris” est mis en place afin de retrouver plus simplement une activité.

## Composants de l'application

Les différents composants d’une application Android sont :

**Activités** : Les activités comportent au minimum une View et sont la base de toute
application sous Android.\
**Fragments** : Les fragments sont des portions réutilisables de code qui font partie
des activités.\
**Adapters** : Les adapters servent à organiser les données dans différentes Views (Listview dans notre cas).

Voici un schéma regroupant tous nos composants :

![PFE (1)](https://user-images.githubusercontent.com/77643801/121333649-47267480-c919-11eb-84b6-815da2de0163.png)

Nous n'avons qu’une seule activité : MainActivity qui est la base de notre application. 
Elle permet la navigation entre les quatres fragments principaux de notre application : HomeFragment, SearchFragment, FavoritesFragment et SettingsFragment.

Nous avons aussi deux adapters :

➢ **CustomListAdapter**, qui sert à organiser chaque évènement dans un item d’une ListView.\
➢ **FavoritesAdapter**, qui permet la gestion des évènements en favoris, dans la base de données FavoritesDB.

Il y ensuite plusieurs classes permettant entre autres de gérer les événements OnClick des fragments.

➢ **Activite** : c’est la classe qui nous permet de gérer l’activité reçue de la base de données.\
➢ **GenerateURL** : c’est cette classe qui génère l’URL dans lequel nous allons récupérer les données. On utilise cette classe seulement pour les choix de tags, de catégories, de prix et enfin de la localisation.\
➢ **FavoritesDB** : base de données des favoris.\
➢ **AppController** : permet de lancer les requêtes.\
➢ **ImageBitmap** : sert à adapter l’image à l’écran du téléphone.





  
