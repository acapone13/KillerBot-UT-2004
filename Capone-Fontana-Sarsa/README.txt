                PROJET BOT 2019

ELEVES: FONTANA, Pedro
        CAPONE, Augusto

ORDINATEUR: ENIB08109-ETRILLE11
~~~~~~~~~~~~~~~~~~~
1-DESCRIPTION

Nous avons choisi d'apprentissage par renforcement parce que nous avons travaillé sur le projet Informatique avec cette sujet et nous avons des approches pour le bot avec ces méthodes.
Au début nous avons pensé de faire DQN mais comme les algorithmes ont devenu trop compliqués pour notre niveau nous avons abaissé notre but et nous avons recommencé avec un simple Q-Learning algorithme pour apprendre avec des actions.

Après de débat nous avons réfléchi d'utiliser un algorithme de SARSA. Sarsa (State-Action-Reward-State’-Action’) c’est un algorithme de type “on-policy”, parce qu'il met à jour la politique en fonction des actions pris.
Sarsa c’est moins flexible que Q-Learning mais qui permet d'avoir des résultats plus réalistes. 
Sarsa prends dans l'état courant un action courant avec ca récompense, pour arriver à un nouvel état et faire une nouveau action.

Pour implémenter SARSA nous avons créé une classe particulière avec des méthodes qui permettent d'obtenir la matrice Q, donner des valeurs de taux d'apprentissage et la politique d’exploration. La méthode principale appelle “Learn” implement l'équation d’actualisation de la matrice Q. Aussi nous avons désigné une interface à partir de laquel nous pouvons implémenté different fonctions d’exploration. A cause du contraintes de temps et des problèmes avec le code, nous avons reste avec une seule type d’exploration: “Greedy Epsilon Exploration”


2-PROBLEMES

Nous croyions que nous étions trop optimistes avec ce que nous voulions
implementer. Nous avons commencé par essayer de créer plusieurs états
avec plusieurs actions pour que l’agent ait plus de liberté, mais au bout 
d’un moment quand le projet ne marchait pas, nous avons decidé de faire
marcher une version plus simple d’abord avec que 4 états et 4 actions. 

Une fois dans cette version simpliste, nous avons remarqué que la matrice
Q ne convergait pas (même si le comportement du bot dans le jeu n’était
pas si mal que ca). Nous avons pris comme réference l’équation de mis 
à jour de Q suivante :

	Q(s,a) = Q(s,a) + lr*(N(s,a)*(r+gamma*argmax(Q(s’,a’)-Q(s,a))

dont N(s,a) est la matrice de fréquences pour les couples « état-action ».
Nous avons remarqué que la raison pour laquelle Q ne convergait pas
c’était que N ne convergait pas. Nous croyons que peut être nous 
n’avons pas bien formulé cette matrice et sur tous l’exemples trouvés dans l’internet elle n’était pas prise en compte, du coup, nous l’avons
retiré. Aprés de ce faire, la matrice Q elle avait des valeurs plus
raisonables (avant nous avions que NaN).

En gros, nous ne sommes pas sûrs que l’algorithme apprend bien mais
nous n’avons jamais eu trop du temps pour l’entraîner.

3- À FAIRE SI NOUS AVONS UN MOIS DE PLUS

D’abord, revoir bien le code pour s’assurer  de la convergance de la matrice Q. Une fois que cela soit fait, ca ne serait pas mal d’ajouter plus d’états et des actions comme dit précedement. 

Modifier la recompense (pour le moment elle ne prende en compte que la
vie du bot et l’armor) par rapport aux autres perceptions tels que le type
d’arme et combien de munition il en a.

Ajouter a l’interface de politique des autres types de recompenses qui 
prennent en compte plus que seulement la meilleure recompense immediate (plustôt d’implementer une recompense accumulée)

Revoir le comportement du bot quand il attaque un enemi (nous avons remarqué qu’il ne tire pas d’une facon intelligente)
