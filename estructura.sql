-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: botpanel
-- ------------------------------------------------------
-- Server version	8.0.45

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
-- Table structure for table `bot`
--

DROP TABLE IF EXISTS `bot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bot` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `mensaje_bienvenida` text,
  `nombre` varchar(255) NOT NULL,
  `numero_whatsapp` varchar(255) DEFAULT NULL,
  `empresa_id` bigint NOT NULL,
  `contextoia` text,
  PRIMARY KEY (`id`),
  KEY `FK9biqay2iearbqnani7g9xlahh` (`empresa_id`),
  CONSTRAINT `FK9biqay2iearbqnani7g9xlahh` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `conversacion`
--

DROP TABLE IF EXISTS `conversacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `conversacion` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cerrado_en` datetime(6) DEFAULT NULL,
  `contacto` varchar(255) NOT NULL,
  `creado_en` datetime(6) NOT NULL,
  `estado` enum('ACTIVA','PENDIENTE','CERRADA') NOT NULL,
  `nombre_contacto` varchar(255) DEFAULT NULL,
  `agente_id` bigint DEFAULT NULL,
  `bot_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKf7muah6oyqpbpdedujlc9jp1h` (`agente_id`),
  KEY `FK66v1s1k4tte9q38495a7nd4dk` (`bot_id`),
  CONSTRAINT `FK66v1s1k4tte9q38495a7nd4dk` FOREIGN KEY (`bot_id`) REFERENCES `bot` (`id`),
  CONSTRAINT `FKf7muah6oyqpbpdedujlc9jp1h` FOREIGN KEY (`agente_id`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `empresa`
--

DROP TABLE IF EXISTS `empresa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `empresa` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activa` bit(1) DEFAULT NULL,
  `creado_en` date NOT NULL,
  `email` varchar(255) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `numero_whatsapp` varchar(255) DEFAULT NULL,
  `token_whatsapp` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_nfu2qgep9eyw4f7jpxoxix8ci` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mensaje`
--

DROP TABLE IF EXISTS `mensaje`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mensaje` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `contenido` text NOT NULL,
  `enviado_en` datetime(6) NOT NULL,
  `origen` enum('BOT','AGENTE','CLIENTE') NOT NULL,
  `conversacion_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhttupgl7j8cb7pspmues7kgac` (`conversacion_id`),
  CONSTRAINT `FKhttupgl7j8cb7pspmues7kgac` FOREIGN KEY (`conversacion_id`) REFERENCES `conversacion` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=268 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `solicitud`
--

DROP TABLE IF EXISTS `solicitud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `solicitud` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `creado_en` datetime(6) DEFAULT NULL,
  `datos` text,
  `estado` enum('PENDIENTE','CONFIRMADA','CANCELADA') DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `notas` text,
  `telefono` varchar(255) DEFAULT NULL,
  `tipo` varchar(255) DEFAULT NULL,
  `bot_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK575sre6dwd0odlo9dlatqgpyu` (`bot_id`),
  CONSTRAINT `FK575sre6dwd0odlo9dlatqgpyu` FOREIGN KEY (`bot_id`) REFERENCES `bot` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `rol` enum('SUPER_ADMIN','ADMIN_EMPRESA','AGENTE') NOT NULL,
  `empresa_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_5171l57faosmj8myawaucatdw` (`email`),
  KEY `FK87ckfs30l64gnivnfk7ywp8l6` (`empresa_id`),
  CONSTRAINT `FK87ckfs30l64gnivnfk7ywp8l6` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-25 14:48:45
