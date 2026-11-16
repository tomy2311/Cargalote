package com.example.cargalote;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AsignacionAdapter extends RecyclerView.Adapter<AsignacionAdapter.VH> {

    // Interfaz para avisar qué fila se tocó
    public interface OnItemClick {
        void onClick(Operacion operacion);
    }

    private final List<Operacion> items;
    private final OnItemClick listener;

    public AsignacionAdapter(List<Operacion> items, OnItemClick listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_asignacion, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Operacion a = items.get(position);

        // Información resumida
        h.tvDia.setText(a.fecha);
        h.tvSector.setText(a.nombreCarga);
        h.tvDetalle.setText(a.idContenedor + " · " + a.ubicacion);

        // Estado: etiqueta de color + barra lateral
        EstadoUtil.aplicarBadge(h.tvEstado, a.estado);
        h.vEstado.setBackgroundColor(EstadoUtil.color(h.itemView.getContext(), a.estado));

        // Seleccionar la operación
        h.itemView.setOnClickListener(v -> listener.onClick(a));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        final TextView tvDia, tvSector, tvDetalle, tvEstado;
        final View vEstado;

        VH(View v) {
            super(v);
            tvDia = v.findViewById(R.id.tvDia);
            tvSector = v.findViewById(R.id.tvSector);
            tvDetalle = v.findViewById(R.id.tvDetalle);
            tvEstado = v.findViewById(R.id.tvEstado);
            vEstado = v.findViewById(R.id.vEstado);
        }
    }
}