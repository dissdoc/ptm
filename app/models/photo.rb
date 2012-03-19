class Photo < ActiveRecord::Base
  belongs_to :album

  has_attached_file :image,
                    :styles => {:icon => "64x64>", :small => "100x63>", :medium => '260x180>', :large => "483x302>" }

  attr_accessible :album_id, :image
end
