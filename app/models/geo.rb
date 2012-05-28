class Geo < ActiveRecord::Base
  belongs_to :photo

  attr_accessible :address, :latitude, :longitude
  geocoded_by :address

  after_validation :geocode, :if => :address_changed?

  has_many :recommend_geos, :dependent => :destroy
end
