/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logingooglerealm;

public class GoogleLoginRealmException extends RuntimeException {
    
	private static final long serialVersionUID = -5999175824710330940L;

	public GoogleLoginRealmException(Exception root) {
        super(root);
    }
}
