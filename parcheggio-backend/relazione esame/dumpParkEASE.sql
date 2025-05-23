CREATE DATABASE  IF NOT EXISTS `webapp` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `webapp`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: webapp
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cities`
--

DROP TABLE IF EXISTS `cities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cities` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `postal_code` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cities`
--

LOCK TABLES `cities` WRITE;
/*!40000 ALTER TABLE `cities` DISABLE KEYS */;
INSERT INTO `cities` VALUES (1,'Milano','20121'),(2,'Roma','00118'),(3,'Ferrara','44121');
/*!40000 ALTER TABLE `cities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parking_spot`
--

DROP TABLE IF EXISTS `parking_spot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parking_spot` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_occupied` bit(1) DEFAULT NULL,
  `spot_number` varchar(255) NOT NULL,
  `city_id` bigint NOT NULL,
  `vehicle_plate` varchar(255) DEFAULT NULL,
  `is_bus_spot` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_parking_spot_city` (`city_id`),
  CONSTRAINT `fk_parking_spot_city` FOREIGN KEY (`city_id`) REFERENCES `cities` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parking_spot`
--

LOCK TABLES `parking_spot` WRITE;
/*!40000 ALTER TABLE `parking_spot` DISABLE KEYS */;
INSERT INTO `parking_spot` VALUES (1,_binary '\0','A1',1,NULL,0),(2,_binary '\0','A2',1,'AA123NN',0),(4,_binary '\0','A3',2,NULL,0),(5,_binary '\0','BUS1',1,'AA123NN',1),(6,_binary '\0','BUS2',2,'AB123BB',1),(7,_binary '\0','BUS3',3,'AA123NN',1),(8,_binary '\0','A4',3,'AA124BC',0),(9,_binary '\0','A5',3,NULL,0),(10,_binary '\0','A6',2,NULL,0),(11,_binary '\0','A7',1,NULL,0);
/*!40000 ALTER TABLE `parking_spot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cost` double NOT NULL,
  `end_time` datetime(6) DEFAULT NULL,
  `start_time` datetime(6) DEFAULT NULL,
  `vehicle_plate` varchar(255) DEFAULT NULL,
  `parking_spot_id` bigint NOT NULL,
  `ticket_id` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `is_bus_reservation` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FKfj9y8aln3wade66dlh8905ei5` (`parking_spot_id`),
  CONSTRAINT `FKfj9y8aln3wade66dlh8905ei5` FOREIGN KEY (`parking_spot_id`) REFERENCES `parking_spot` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (21,50,'2025-05-09 18:00:00.000000','2025-05-08 17:00:00.000000','AA123NN',1,'2be5f43e-67f7-4dba-920b-e58b60dd8a80','test@email.com',0),(23,2,'2025-04-29 12:30:00.000000','2025-04-29 10:35:00.000000','AA123NN',4,'fbeec62a-8e3d-4def-b3d4-ec9bc6665836','test@email.com',0),(24,25,'2025-05-02 13:00:00.000000','2025-05-01 13:00:00.000000','AB123BB',2,'7066553a-f6a2-4e04-be41-2da796ade3ce',NULL,0),(25,4,'2025-05-01 13:00:00.000000','2025-05-01 11:00:00.000000','AB123BB',1,'a5c99e26-97db-4384-9593-f34828e464b0',NULL,0),(26,4,'2025-05-01 11:00:00.000000','2025-05-01 09:00:00.000000','AB123BB',1,'46c0ee8a-9477-40b8-8f36-c4c2127e1a61',NULL,0),(27,2,'2025-05-10 14:00:00.000000','2025-05-10 12:30:00.000000','AA123NN',1,'f8ca6ca1-d6fd-48e0-8ebc-050e997f53f5',NULL,0),(29,25,'2025-05-15 08:00:00.000000','2025-05-14 19:00:00.000000','AA123NN',5,'c88ca569-e2f7-48b9-bb81-896a577841d0','ciao@busexpress.com',0),(30,14,'2025-05-14 17:30:00.000000','2025-05-14 09:55:00.000000','AA123NN',2,'61a8267d-8e75-43d2-85e5-0c621b9afa0f','samuele@mail.com',0),(31,25,'2025-05-13 09:00:00.000000','2025-05-12 18:00:00.000000','AA123NN',7,'6b214782-11e2-47b8-9d9b-7f2743d85c27','cc@busexpress.com',0),(32,25,'2025-05-17 10:00:00.000000','2025-05-16 10:00:00.000000','AA124BC',8,'fa6536d0-c6b2-4701-ba38-0bb37897c3ad','pollo@mail.com',0),(33,2,'2025-05-29 14:45:00.000000','2025-05-29 13:40:00.000000','AB123BB',9,'dc84b400-c0e2-4ee0-b775-8bf2fb0e8bc7','pollo@mail.com',0),(34,25,'2025-05-23 06:00:00.000000','2025-05-22 17:00:00.000000','AB123BB',5,'29419f31-3c72-4965-9a81-c39820081b86','ciao@busexpress.com',0),(35,50,'2025-05-22 12:30:00.000000','2025-05-21 10:30:00.000000','AA124BC',1,'623b43a4-61ca-4b0b-a551-243463be2bad','prova@mail.com',0),(36,4,'2025-05-23 15:00:00.000000','2025-05-23 13:00:00.000000','AA123NN',1,'18b39062-7883-4d47-99d0-6e55d942310d','test@email.com',0),(38,25,'2025-05-31 07:00:00.000000','2025-05-30 17:00:00.000000','AB123BB',6,'0834b3c5-0a3e-49de-9017-933acb4920ba','prova@busexpress.it',0),(39,3,'2025-05-30 14:30:00.000000','2025-05-30 13:00:00.000000','AA123NN',9,'c3392efd-1da8-419f-840d-aaf48805f628','prova@mail.it',0),(40,5,'2025-06-03 16:00:00.000000','2025-06-03 13:40:00.000000','AA123NN',5,'3d7c6133-63d7-4eb9-97dd-c05e4511de70','prova@busexpress.it',0);
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'mail@prova.com','$2a$10$glkdBZ9/BzAn3HFfSZxMueSD1eD3bh7Ng5p1Z5RlJe/hQvJ0oDHaq'),(2,'pollo@mail.com','$2a$10$UmS7LboBMHK/UD8YlpdtIuZcwrjabnXC9zODIX3dtgZY34GYZkai.'),(3,'prova@mail.com','$2a$10$e3BCarTer2D.u0V9UzI8vufVUubbTuKSuClfBgek6NMDytE1i/vBC'),(4,'test@email.com','$2a$10$cjRpCm0UoFbkpZb9RfQ3fuzy9Q/3I4V9MuUqYAFXxdYuOmfA6HJHG'),(5,'prova@mail.it','$2a$10$sBqwVrsaXz5SQB81yeVBiOq3XJ94BggWYZNtttvmkQ/bL8nORJ94i');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'webapp'
--

--
-- Dumping routines for database 'webapp'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-23 11:53:33
