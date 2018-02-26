package getset;

import java.util.Locale;

public class ServiceRowdata {
	 	public boolean isclicked=false;
	    public int index;
	    public String serviceid;
	    public String strAmount;
	public String strDiscount;
	    public String servicename;
	    
	    public ServiceRowdata(boolean isclicked,int index,String serviceid,String strAmount,String strDiscount,String serviceName)
	    {
	        this.index=index;
	        this.isclicked=isclicked;
	        this.serviceid = serviceid;
	        /*this.fanId=fanId;*/
	        this.strAmount=strAmount;
	        this.servicename = serviceName;
			this.strDiscount = strDiscount;
	    }
	    
	    public int getIndex()
	    {
	    	return index;
	    }
	    public void setIndex(int index) {
			this.index = index;
		}
	    
	    public String getAmount()
	    {
	    	return strAmount;
	    }
	public String getDiscountAmount(){
		Double amount = Double.parseDouble(strAmount);
		Double discount = Double.parseDouble(strDiscount) * amount / 100;
		amount = amount - discount;
		return String.format(Locale.ENGLISH, "%.2f", amount);
	}
	    public void setAmount(String amount) {
			this.strAmount = amount;
		}

	public String getDiscount()
	{
		return strDiscount;
	}
	public void setDiscount(String discount) {
		this.strDiscount = discount;
	}

	    public String getServiceId()
	    {
	    	return serviceid;
	    }
	    public void setServiceId(String id) {
			this.serviceid = id;
		}
	    
	    public String getServiceName()
	    {
	    	return servicename;
	    }
	    public void setServiceName(String service) {
			this.servicename = service;
		}
	    
	    public boolean isChecked() {
	    	  return isclicked;
	    }
	    public void setChecked(boolean selected) {
	    	  this.isclicked = selected;
	    }
	    	  
	    
	    
}
