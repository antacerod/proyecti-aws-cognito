package com.proyecti.blog.cognito.configuration.enumeration;

public class IdentityProvider 
{    
	private static final String AWS_COGNITO_PROVIDER = "cognito-identity.amazonaws.com";
	private static final String AWS_PROVIDER = "www.amazon.com";
	private static final String FACEBOOK_PROVIDER = "graph.facebook.com";
	private static final String GOOGLE_PROVIDER = "accounts.google.com";
	
	public enum IdentityProviderEnum
	{
		AWS_COGNITO(1), AWS(2), FACEBOOK(3), GOOGLE(4);
        
		private int value;
	       
        private IdentityProviderEnum(int value) 
        {
            this.value = value;
        }
	
	 
		public static IdentityProviderEnum fromInteger(final int status) 
	    {
			switch(status) 
	        {
	        	case 1:
	         		return AWS_COGNITO;
	         	case 2:
	         		return AWS;
	         	case 3:
	         		return FACEBOOK;
	         	case 4:
	         		return GOOGLE;
	         	default:
	         		return AWS_COGNITO;
	         }
	     }

		public long getValue()
		{
			return value;
		}
		
		public String getValueAsString() 
		{
			final String valueAsString;
			
			switch(value) 
	        {
	        	case 1:
	        		valueAsString = AWS_COGNITO_PROVIDER;
	        		break;
	        		
	         	case 2:
	        		valueAsString = AWS_PROVIDER;
	        		break;
	        		
	         	case 3:
	        		valueAsString = FACEBOOK_PROVIDER;
	        		break;
	        		
	          	case 4:
	        		valueAsString = GOOGLE_PROVIDER;
	        		break;
	        		
	         	default:
	        		valueAsString = AWS_COGNITO_PROVIDER;
	        }
			
			return valueAsString;
		}
	}
}
