~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		PROJET BOT 2019

ELEVES: FONTANA, Pedro
	CAPONE, Augusto

ORDINATEUR: ENIB08109-ETRILLE11

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

1-DESCRIPTION:

Nous avons créé un automate défini par une machine
à états parmi lesquels nous pouvons trouver:

- IDLE:
	L'état par default si les autres états ne sont pas 
	possibles c'est IDLE. Sur cet état le bot va
	essayer de prendre les items qui lui permettent 
	d'améliorer.	

- HURT:
	Dans cet état la fonction stateHurt est appelé. 
	Cette fonction integre dedans les fonctions
	stateMedKit() et stateHit() du HunterBot.
	Le bot va prioriser la recherche d'un Medkit s'il
	est plus blessé qu'un seuil defini par 
	l'utilisateur.
	S'il ne trouve pas de medkit, il va changer son état
	à IDLE. 
	Si le bot est blessé et l'ennemi ne peut pas lui
	voir, il va essayer de courir dans la direction 
	opposée.

- ATTACK:

	L'état Attack implement la fonction stateAttack(). 
	Cet état est appellé:
	1) Si le bot est blessé, il trouve quelqu'enemi et
	 son arme est bien chargé.
	2) S'il senses que quelq'un est en train de lui tuer. 
	Dedans la fonction stateAttack() le bot cherche un 
	nouveau ennemi, arret de tirer s'il n'arrive pas à
	regarder son ennemi ou essai d'aller vers l'ennemi 
	s'il est loin ou il n'arrive pas à le voir.
	 

- SEARCH:
	L'état Search appel la fonction stateSearch() qui
	permet de naviger sur des points avec une algorithme
	A*. S'il trouve un ennemi, le bot va s'approcher à
	le target.

Chaqu'état a sa prope fonction définie dans la classe
KillerBot.

Pour implementer cette machine à états nous avons créé
une classe State dans laquelle se trouvent les 
fonctions:

- currentState(KillerBot bot):
	
	Cette fonction éxécute les actions pertinent à
	l'état actuel du bot qu'il y a comme parametre.
	Cette fonction est abstract dans la classe et
	elle est définie dans une enumeration en 
	dépendant de l'état du bot.

- nextState(Killer bot):

	Cette fonction gére les transitions entre des
	états en regardant les variables du bot comme:
	- La vitalité.
	- Les armes.
	- Les jouers visibles.
	Comme paramettre  de retour il y a l'état
	suivant

Aussi nous avons implemente dans la logique du bot,
un algorithme aui permet de gérer le score du bot.

2-PROBLEMES:

Au début du projet, nous avons pris beaucoup du temps
en regardant la facon comment pogamut marche. Cela
nous a laissé avec pas trop du temps pour travailler
sur le projet. Une fois que nous avons bien compris
pogamut, nous sommes partis à faire l'automate depuis
un EmptyBot (nous pensions que le but de la prémiere
partie c'était d'arriver à un comportement similair
à celui-là du HunterBot). Quand nous avons trouvé que
cela n'était pas vrai, nous avons recommencé le projet
en utilisant comme base le code du HunterBot.

3-MODIFICATIONS A FAIRE:

Nous avons trouvé que parfois le bot à un comportement
stupide ou bizarre face à certaines situations. Nous
devons améliorer nos états pour que le bot soit prêt
à les confronter. Nous avons aussi eu l'idée d'implementer 
un algorithme de apprentissage par reinforcement pour gérer
les transitions des états mais nous n'avons eu pas le 
temps pour le faire. Cela reste comment notre objectif 
pour la déuxièmme partie.
