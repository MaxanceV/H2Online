#!/bin/bash

# Étape 1 : Vérifier si Docker est en cours d'exécution
if ! docker info >/dev/null 2>&1; then
  echo "Docker n'est pas démarré. Veuillez démarrer Docker et réessayer."
  exit 1
fi

# Étape 2 : Démarrer les conteneurs Docker
echo "Démarrage des conteneurs Docker..."
docker-compose up -d

# Vérifier si les conteneurs se sont bien lancés
if [ $? -ne 0 ]; then
  echo "Erreur lors du démarrage des conteneurs Docker. Veuillez vérifier votre configuration."
  exit 1
fi

# Étape 3 : Attendre que MySQL soit prêt
echo "Attente que MySQL soit prêt..."
until docker exec h2online-mysql mysql -u user -ppassword -e "SHOW DATABASES;" >/dev/null 2>&1; do
  echo -n "."
  sleep 1
done
echo -e "\nMySQL est prêt !"

# Étape 4 : Lancer l'application Java
echo "Lancement de l'application Java..."
mvn exec:java -Dexec.mainClass="Main.MainLaunched"

# Étape 5 : Arrêter les conteneurs Docker lorsque l'application se termine
echo "Arrêt des conteneurs Docker..."
docker-compose down

echo "Tout s'est terminé correctement."
