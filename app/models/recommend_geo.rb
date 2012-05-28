class RecommendGeo < ActiveRecord::Base
  belongs_to :geo
  belongs_to :user

  attr_accessible :address, :latitude, :longitude
  geocoded_by :address

  after_validation :geocode, :if => :address_changed?
end
