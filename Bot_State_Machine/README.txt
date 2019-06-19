~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		PROJET BOT 2019

ELEVES: FONTANA, Pedro
	CAPONE, Augusto

ORDINATEUR: ENIB08109-ETRILLE11

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

1-DESCRIPTION:

Nous avons cr�� un automate d�fini par une machine
� �tats parmi lesquels nous pouvons trouver:

- IDLE:
	L'�tat par default si les autres �tats ne sont pas 
	possibles c'est IDLE. Sur cet �tat le bot va
	essayer de prendre les items qui lui permettent 
	d'am�liorer.	

- HURT:
	Dans cet �tat la fonction stateHurt est appel�. 
	Cette fonction integre dedans les fonctions
	stateMedKit() et stateHit() du HunterBot.
	Le bot va prioriser la recherche d'un Medkit s'il
	est plus bless� qu'un seuil defini par 
	l'utilisateur.
	S'il ne trouve pas de medkit, il va changer son �tat
	� IDLE. 
	Si le bot est bless� et l'ennemi ne peut pas lui
	voir, il va essayer de courir dans la direction 
	oppos�e.

- ATTACK:

	L'�tat Attack implement la fonction stateAttack(). 
	Cet �tat est appell�:
	1) Si le bot est bless�, il trouve quelqu'enemi et
	 son arme est bien charg�.
	2) S'il senses que quelq'un est en train de lui tuer. 
	Dedans la fonction stateAttack() le bot cherche un 
	nouveau ennemi, arret de tirer s'il n'arrive pas �
	regarder son ennemi ou essai d'aller vers l'ennemi 
	s'il est loin ou il n'arrive pas � le voir.
	 

- SEARCH:
	L'�tat Search appel la fonction stateSearch() qui
	permet de naviger sur des points avec une algorithme
	A*. S'il trouve un ennemi, le bot va s'approcher �
	le target.

Chaqu'�tat a sa prope fonction d�finie dans la classe
KillerBot.

Pour implementer cette machine � �tats nous avons cr��
une classe State dans laquelle se trouvent les 
fonctions:

- currentState(KillerBot bot):
	
	Cette fonction �x�cute les actions pertinent �
	l'�tat actuel du bot qu'il y a comme parametre.
	Cette fonction est abstract dans la classe et
	elle est d�finie dans une enumeration en 
	d�pendant de l'�tat du bot.

- nextState(Killer bot):

	Cette fonction g�re les transitions entre des
	�tats en regardant les variables du bot comme:
	- La vitalit�.
	- Les armes.
	- Les jouers visibles.
	Comme paramettre  de retour il y a l'�tat
	suivant

Aussi nous avons implemente dans la logique du bot,
un algorithme aui permet de g�rer le score du bot.

2-PROBLEMES:

Au d�but du projet, nous avons pris beaucoup du temps
en regardant la facon comment pogamut marche. Cela
nous a laiss� avec pas trop du temps pour travailler
sur le projet. Une fois que nous avons bien compris
pogamut, nous sommes partis � faire l'automate depuis
un EmptyBot (nous pensions que le but de la pr�miere
partie c'�tait d'arriver � un comportement similair
� celui-l� du HunterBot). Quand nous avons trouv� que
cela n'�tait pas vrai, nous avons recommenc� le projet
en utilisant comme base le code du HunterBot.

3-MODIFICATIONS A FAIRE:

Nous avons trouv� que parfois le bot � un comportement
stupide ou bizarre face � certaines situations. Nous
devons am�liorer nos �tats pour que le bot soit pr�t
� les confronter. Nous avons aussi eu l'id�e d'implementer 
un algorithme de apprentissage par reinforcement pour g�rer
les transitions des �tats mais nous n'avons eu pas le 
temps pour le faire. Cela reste comment notre objectif 
pour la d�uxi�mme partie.
