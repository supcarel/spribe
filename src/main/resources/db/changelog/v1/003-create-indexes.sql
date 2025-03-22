CREATE INDEX idx_bookings_unit_id_dates ON bookings (unit_id, start_date, end_date)
WHERE status IN ('PENDING', 'CONFIRMED');

CREATE INDEX idx_units_search ON units (
    is_available,
    unit_type_id,
    rooms_count,
    floor,
    base_price
);