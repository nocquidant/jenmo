-- MySQL dump 10.11
--
-- Host: localhost    Database: openflow
-- ------------------------------------------------------
-- Server version	5.0.51a-community-nt-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `class`
--

DROP TABLE IF EXISTS `class`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `class` (
  `idClass` int(11) NOT NULL auto_increment,
  `strClass` varchar(128) NOT NULL,
  PRIMARY KEY  (`idClass`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `class`
--

LOCK TABLES `class` WRITE;
/*!40000 ALTER TABLE `class` DISABLE KEYS */;
/*!40000 ALTER TABLE `class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class_property`
--

DROP TABLE IF EXISTS `class_property`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `class_property` (
  `idProperty` int(11) NOT NULL,
  `idClass` int(11) NOT NULL,
  PRIMARY KEY  (`idClass`,`idProperty`),
  KEY `idProperty` (`idProperty`),
  CONSTRAINT `class_property_ibfk_1` FOREIGN KEY (`idProperty`) REFERENCES `property` (`idProperty`),
  CONSTRAINT `class_property_ibfk_2` FOREIGN KEY (`idClass`) REFERENCES `class` (`idClass`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `class_property`
--

LOCK TABLES `class_property` WRITE;
/*!40000 ALTER TABLE `class_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `class_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `measurement`
--

DROP TABLE IF EXISTS `measurement`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `measurement` (
  `idNode` int(10) unsigned NOT NULL default '0',
  `idProperty` int(10) unsigned NOT NULL default '0',
  `idDate` int(10) unsigned NOT NULL default '0',
  `SpacialX` smallint(5) unsigned NOT NULL default '0',
  `SpatialY` smallint(5) unsigned NOT NULL default '0',
  `SpatialZ` smallint(5) unsigned NOT NULL default '0',
  `mesure` float NOT NULL default '0',
  PRIMARY KEY  (`idNode`,`idProperty`,`idDate`,`SpacialX`,`SpatialY`,`SpatialZ`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `measurement`
--

LOCK TABLES `measurement` WRITE;
/*!40000 ALTER TABLE `measurement` DISABLE KEYS */;
/*!40000 ALTER TABLE `measurement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `node`
--

DROP TABLE IF EXISTS `node`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `node` (
  `idNode` bigint(20) NOT NULL auto_increment,
  `strComment` varchar(255) character set utf8 collate utf8_bin default NULL,
  `dtCreation` datetime default NULL,
  `dtUpdate` datetime default NULL,
  `idOwner` int(10) unsigned NOT NULL,
  `idNodeType` int(10) unsigned NOT NULL,
  `idBObject` int(10) unsigned NOT NULL,
  PRIMARY KEY  USING BTREE (`idNode`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `node`
--

LOCK TABLES `node` WRITE;
/*!40000 ALTER TABLE `node` DISABLE KEYS */;
/*!40000 ALTER TABLE `node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `node_parent`
--

DROP TABLE IF EXISTS `node_parent`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `node_parent` (
  `idNode` int(11) NOT NULL,
  `idNodeParent` int(11) NOT NULL,
  PRIMARY KEY  (`idNode`,`idNodeParent`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `node_parent`
--

LOCK TABLES `node_parent` WRITE;
/*!40000 ALTER TABLE `node_parent` DISABLE KEYS */;
/*!40000 ALTER TABLE `node_parent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `node_property`
--

DROP TABLE IF EXISTS `node_property`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `node_property` (
  `idProperty` int(11) NOT NULL,
  `idNode` int(11) NOT NULL,
  `strValue` varchar(128) default NULL,
  PRIMARY KEY  (`idNode`,`idProperty`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `node_property`
--

LOCK TABLES `node_property` WRITE;
/*!40000 ALTER TABLE `node_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `node_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `node_revision`
--

DROP TABLE IF EXISTS `node_revision`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `node_revision` (
  `idNode` int(11) NOT NULL,
  `idNodePrevious` int(11) NOT NULL,
  PRIMARY KEY  (`idNode`,`idNodePrevious`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `node_revision`
--

LOCK TABLES `node_revision` WRITE;
/*!40000 ALTER TABLE `node_revision` DISABLE KEYS */;
/*!40000 ALTER TABLE `node_revision` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `node_type`
--

DROP TABLE IF EXISTS `node_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `node_type` (
  `idNodeType` int(11) NOT NULL auto_increment,
  `strNodeType` varchar(128) NOT NULL,
  PRIMARY KEY  (`idNodeType`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `node_type`
--

LOCK TABLES `node_type` WRITE;
/*!40000 ALTER TABLE `node_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `node_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `node_type_property`
--

DROP TABLE IF EXISTS `node_type_property`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `node_type_property` (
  `idProperty` int(11) NOT NULL,
  `idNodeType` int(11) NOT NULL,
  PRIMARY KEY  (`idNodeType`,`idProperty`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `node_type_property`
--

LOCK TABLES `node_type_property` WRITE;
/*!40000 ALTER TABLE `node_type_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `node_type_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `property`
--

DROP TABLE IF EXISTS `property`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `property` (
  `idProperty` int(11) NOT NULL auto_increment,
  `strProperty` varchar(128) NOT NULL,
  PRIMARY KEY  USING BTREE (`idProperty`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `property`
--

LOCK TABLES `property` WRITE;
/*!40000 ALTER TABLE `property` DISABLE KEYS */;
/*!40000 ALTER TABLE `property` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2008-02-11 10:03:09
