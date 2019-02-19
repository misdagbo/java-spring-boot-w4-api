/**
 * 
 */
package com.w4.api.contracts;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.w4.api.infrastructures.*;
import com.w4.api.models.*;

/**
 * @author frederic
 *
 */
@XmlRootElement
public class CommentaireResponse extends ResponseBase {
	
    private List<Commentaire> commentaires;
	/**
	 * 
	 */
	public CommentaireResponse() {
		super();
		// TODO Auto-generated constructor stub
		commentaires = new ArrayList<>();
	}
	/**
	 * @return the commentaires
	 */
	public List<Commentaire> getCommentaires() {
		return commentaires;
	}
	/**
	 * @param commentaires the commentaires to set
	 */
	public void setCommentaires(List<Commentaire> commentaires) {
		this.commentaires = commentaires;
	}
}
