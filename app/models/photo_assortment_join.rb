class PhotoAssortmentJoin < ActiveRecord::Base
  belongs_to :photo
  belongs_to :assortment

  validates :photo_id, :assortment_id, :presence => true
end
