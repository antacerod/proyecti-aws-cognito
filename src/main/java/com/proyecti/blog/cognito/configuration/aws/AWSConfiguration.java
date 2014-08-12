package com.proyecti.blog.cognito.configuration.aws;

import org.springframework.context.annotation.Bean;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient;

/**
 * @author Antonio Acevedo (Proyecti)
 *
 * @see <a href="http://mobile.awsblog.com/post/TxR1UCU80YEJJZ/Using-the-Amazon-Cognito-Credentials-Provider">http://mobile.awsblog.com/post/TxR1UCU80YEJJZ/Using-the-Amazon-Cognito-Credentials-Provider</a>
 */
public class AWSConfiguration 
{    
	@Bean
	public AmazonCognitoIdentityClient amazonCognitoIdentityClient()
	{
		final String accountId = "The AWS accountId for the account with Amazon Cognito";
		final String identityPoolId = "The Amazon Cogntio identity pool to use";
		final String unauthRoleArn = "The ARN of the IAM Role that will be assumed when unauthenticated";
		final String authRoleArn = "The ARN of the IAM Role that will be assumed when authenticated";
		
		final CognitoCredentialsProvider cognitoCredentialsProvider = new CognitoCredentialsProvider(accountId, identityPoolId, unauthRoleArn, authRoleArn);

		return new AmazonCognitoIdentityClient(cognitoCredentialsProvider);
	}
}
