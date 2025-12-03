import React from "react";
import { Link } from "react-router-dom";
import "../css/ShopCard.css";

export default function ShopCard({ shop }) {
  return (
    <div className="shop-card">
      <div className="shop-image">
        <img src={shop.image} alt={shop.name} />
      </div>
      <div className="shop-info">
        <h3>{shop.name}</h3>
        <p className="shop-type">{shop.type}</p>
        <p className="shop-location">📍 {shop.city}</p>
        <p className="shop-description">{shop.description}</p>
        <p className="shop-hours">⏰ {shop.hours}</p>
        <Link to={`/shop/${shop.id}`} state={{ shop }}>
          <button className="book-now-btn">Book Now</button>
        </Link>
      </div>
    </div>
  );
}
