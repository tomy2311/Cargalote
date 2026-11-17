package com.example.cargalote;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AsignacionAdapter extends RecyclerView.Adapter<AsignacionAdapter.VH> {

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
        Operacion o = items.get(position);

        // Información resumida
        h.tvFecha.setText(o.fecha);
        h.tvCarga.setText(o.nombreCarga);
        h.tvDetalle.setText(o.idContenedor + " · " + o.ubicacion);

        // Estado: etiqueta + barra lateral
        EstadoUtil.aplicarBadge(h.tvEstado, o.estado);
        h.vEstado.setBackgroundColor(EstadoUtil.color(h.itemView.getContext(), o.estado));

        // Selección
        h.itemView.setOnClickListener(v -> listener.onClick(o));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        final TextView tvFecha, tvCarga, tvDetalle, tvEstado;
        final View vEstado;

        VH(View v) {
            super(v);
            tvFecha = v.findViewById(R.id.tvDia);
            tvCarga = v.findViewById(R.id.tvSector);
            tvDetalle = v.findViewById(R.id.tvDetalle);
            tvEstado = v.findViewById(R.id.tvEstado);
            vEstado = v.findViewById(R.id.vEstado);
        }
    }
}