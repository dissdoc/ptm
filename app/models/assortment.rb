class Assortment < ActiveRecord::Base
  belongs_to :user

  has_many :photo_assortment_joins, :dependent => :destroy

  has_many :photos,
      :through => :photo_assortment_joins,
      :class_name => "Photo",
      :source => :photo

  validates :user_id, :name,  :presence => true
end
