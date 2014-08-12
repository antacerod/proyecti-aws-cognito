package com.proyecti.blog.cognito.services.dto;

import com.amazonaws.services.s3.AmazonS3Client;

public class WifResponseDTO
{
	private final String subjectFromWIF;
	private final AmazonS3Client amazonS3Client;
	
	public WifResponseDTO(String subjectFromWIF, AmazonS3Client amazonS3Client)
	{
		this.subjectFromWIF = subjectFromWIF;
		this.amazonS3Client = amazonS3Client;
	}

	public String getSubjectFromWIF() {
		return subjectFromWIF;
	}

	public AmazonS3Client getAmazonS3Client() {
		return amazonS3Client;
	}
}