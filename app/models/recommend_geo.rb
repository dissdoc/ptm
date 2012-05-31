class RecommendGeo < ActiveRecord::Base
  belongs_to :photo
  belongs_to :user

  attr_accessible :address, :latitude, :longitude
  geocoded_by :address

  validates :user_id, :photo_id, :presence => true

  after_validation :geocode, :if => :address_changed?
end
