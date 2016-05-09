/*
SQLyog 企业版 - MySQL GUI v8.14 
MySQL - 5.5.20 : Database - saopt
*********************************************************************
*/
/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`saopt` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `saopt`;

/*Table structure for table `t_change` */

DROP TABLE IF EXISTS `t_change`;

CREATE TABLE `t_change` (
  `chgID` bigint(20) NOT NULL AUTO_INCREMENT,
  `runID` varchar(160) NOT NULL,
  `genID` varchar(3) NOT NULL,
  `optID` varchar(50) NOT NULL,
  `DoFID` varchar(2) NOT NULL,
  `elemName` varchar(100) NOT NULL,
  `value` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`chgID`),
  KEY `FK_t_change` (`runID`,`genID`,`optID`),
  CONSTRAINT `FK_t_change` FOREIGN KEY (`runID`, `genID`, `optID`) REFERENCES `t_run` (`runID`, `genID`, `optID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22597 DEFAULT CHARSET=utf8;

/*Data for the table `t_change` */

/*Table structure for table `t_dof` */

DROP TABLE IF EXISTS `t_dof`;

CREATE TABLE `t_dof` (
  `Id` varchar(2) NOT NULL,
  `Name` varchar(40) DEFAULT NULL,
  `Description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `t_dof` */

/*Table structure for table `t_idx` */

DROP TABLE IF EXISTS `t_idx`;

CREATE TABLE `t_idx` (
  `runID` varchar(160) NOT NULL,
  `genID` varchar(3) NOT NULL,
  `optID` varchar(50) NOT NULL,
  `paraTypeNm` varchar(100) NOT NULL,
  `elemName` varchar(100) DEFAULT NULL,
  `paraValue` float DEFAULT '0',
  `idxID` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`idxID`),
  KEY `FK_t_idx` (`runID`,`genID`,`optID`),
  CONSTRAINT `FK_t_idx` FOREIGN KEY (`runID`, `genID`, `optID`) REFERENCES `t_run` (`runID`, `genID`, `optID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `t_idx` */

/*Table structure for table `t_run` */

DROP TABLE IF EXISTS `t_run`;

CREATE TABLE `t_run` (
  `runID` varchar(160) NOT NULL,
  `genID` varchar(3) NOT NULL,
  `optID` varchar(50) NOT NULL,
  `runTime` datetime DEFAULT NULL,
  `Code` varchar(500) DEFAULT NULL,
  `RestValue` float DEFAULT '0',
  `ReltValue` float(12,10) DEFAULT '0',
  `costValue` float(12,7) DEFAULT '0',
  PRIMARY KEY (`runID`,`genID`,`optID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `t_run` */

/*Table structure for table `t_temp_run` */

DROP TABLE IF EXISTS `t_temp_run`;

CREATE TABLE `t_temp_run` (
  `runID` varchar(160) NOT NULL,
  `genID` varchar(3) NOT NULL,
  `optID` varchar(50) NOT NULL,
  `runTime` datetime DEFAULT NULL,
  `Code` varchar(500) DEFAULT NULL,
  `RestValue` float DEFAULT '0',
  `ReltValue` float(12,10) DEFAULT '0',
  `costValue` float(12,7) DEFAULT '0',
  PRIMARY KEY (`runID`,`genID`,`optID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `t_temp_run` */

/* Function  structure for function  `getChgElems` */

/*!50003 DROP FUNCTION IF EXISTS `getChgElems` */;
DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` FUNCTION `getChgElems`(in_runID varchar(160),in_genID VARCHAR(3),in_optID VARCHAR(50),in_elemNumPerLine int) RETURNS mediumtext CHARSET utf8
BEGIN
      DECLARE _elemName varchar(100) default ""; -- 元素名称
      
      DECLARE _value    VARCHAR(50) DEFAULT "";  -- 元素修改后的值
      DECLARE output VARCHAR(80000) default "";  -- 输出字符串
      declare done int default -1;               -- 游标是否读结束
      DECLARE cnt int default 1;                 -- 
       
       
   /* SELECT genIDsInStr="";*/
      /*定义游标从T_change表中获取修改元素的名称和值 */
      DECLARE csrChg CURSOR FOR  SELECT  elemName,t_change.value  FROM t_change where runID=in_runID AND genID=in_genID AND optID=in_optID;
      /*定义done=1是说明指标已读结束*/
      declare continue handler for not found set done=1;
      OPEN csrChg;-- 打开游标
      
      /* 循环读取游标数据*/
       myLoop: LOOP
        
        fetch csrChg into _elemName,_value;
          
        if done = 1 then -- 读是否结束   
           leave myLoop;  
        end if;  
        
        /* 根据一行输出几个修改元素，进行输出字符串的格式工作  */             
        if cnt < in_elemNumPerLine  Then 
          begin
            if cnt=1 then
              SET output=CONCAT(output,_elemName,"=",_value);
            else
              SET output=CONCAT(output," , ",_elemName,"=",_value);
            end if;
            set cnt=cnt+1; 
          end;  
        else
          begin
           SET output=CONCAT(output," , ",_elemName,"=",_value,CHAR(13));
           set cnt=1;
         end;
        end if;        
        
      end LOOP myLoop;
        
        
      close csrChg;
      return output;
    END */$$
DELIMITER ;

/* Function  structure for function  `getChgElems2` */

/*!50003 DROP FUNCTION IF EXISTS `getChgElems2` */;
DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` FUNCTION `getChgElems2`(in_runID varchar(160),in_genID VARCHAR(3),in_optID VARCHAR(50),in_elemNumPerLine int) RETURNS mediumtext CHARSET utf8
BEGIN
      DECLARE _elemName varchar(100) default ""; -- 元素名称
      
      DECLARE _value    VARCHAR(50) DEFAULT "";  -- 元素修改后的值
      DECLARE output VARCHAR(80000) default "";  -- 输出字符串
      declare done int default -1;               -- 游标是否读结束
      DECLARE cnt int default 1;                 -- 
       
       
   /* SELECT genIDsInStr="";*/
      /*定义游标从T_change表中获取修改元素的名称和值 */
      DECLARE csrChg CURSOR FOR  SELECT  elemName,t_change.value  FROM t_change where runID=in_runID AND genID=in_genID AND optID=in_optID;
      /*定义done=1是说明指标已读结束*/
      declare continue handler for not found set done=1;
      OPEN csrChg;-- 打开游标
      
      /* 循环读取游标数据*/
       myLoop: LOOP
        
        fetch csrChg into _elemName,_value;
          
        if done = 1 then -- 读是否结束   
           leave myLoop;  
        end if;  
        
        /* 根据一行输出几个修改元素，进行输出字符串的格式工作  */             
        if cnt < in_elemNumPerLine  Then 
          begin
            if cnt=1 then
              SET output=CONCAT(output,_elemName,"=",_value);
            else
              SET output=CONCAT(output," , ",_elemName,"=",_value);
            end if;
            set cnt=cnt+1; 
          end;  
        else
          begin
           SET output=CONCAT(output," , ",_elemName,"=",_value,CHAR(13));
           set cnt=1;
         end;
        end if;        
        
      end LOOP myLoop;
        
        
      close csrChg;
      return output;
    END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
