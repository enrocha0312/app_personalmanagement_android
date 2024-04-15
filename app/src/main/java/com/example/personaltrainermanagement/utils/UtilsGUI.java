package com.example.personaltrainermanagement.utils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.example.personaltrainermanagement.R;

public class UtilsGUI {
    public static void avisar(Context contexto, int idTexto){
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle(R.string.aviso);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setMessage(idTexto);
        builder.setNeutralButton(R.string.ok, null);
        AlertDialog alert = builder.create();
        alert.show();
    }
    public static void perguntarAoUsuarioSobreAcao(Context contexto,
                                                   String mensagem,
                                                   DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle(R.string.verification);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(mensagem);
        builder.setPositiveButton(R.string.sim, listener);
        builder.setPositiveButton(R.string.nao, listener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

