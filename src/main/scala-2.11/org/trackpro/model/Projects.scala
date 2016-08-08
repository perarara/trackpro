package org.trackpro.model

import org.trackpro.persistence.DataType.DataType

/**
  * Created by Petar on 7/31/2016.
  */



case class User(id: Long=0L,firstName:String,lastName:String,email:String,password:String)
case class Project(id: Long,title:String,userId: Long)
case class ProjectData(id: Long,projectId:Long,dataType:DataType)
case class ProjectFinance(id: Long,projectId:Long,title:String,amount:BigDecimal)
case class Map(id: Long,projectId:Long,mapTitle:String,think:String,feel:String,hear:String,see:String,pain:String,gain:String,
               avgTransAmount:BigDecimal,avgUpSaleAmount:BigDecimal,supportExpectations:String,mainConcerns:String,paygrade:String,
               advertisingLocations:String,gatheringPlaces:String)
